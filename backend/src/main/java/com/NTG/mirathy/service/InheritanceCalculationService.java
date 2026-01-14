package com.NTG.mirathy.service;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.DTOs.request.InheritanceCalculationRequest;
import com.NTG.mirathy.DTOs.response.FullInheritanceResponse;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.User;
import com.NTG.mirathy.exceptionHandler.InvalidInheritanceCaseException;
import com.NTG.mirathy.rule.InheritanceRule;
import com.NTG.mirathy.util.InheritanceCase;
import com.NTG.mirathy.util.InheritanceUtils;
import com.NTG.mirathy.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InheritanceCalculationService {

    private final List<InheritanceRule> rules;
    private final ArabicInheritanceTextService arabicInheritanceTextService;
    private final SecurityUtil securityUtil;
    private final InheritanceProblemService inheritanceProblemService;

    // ================== MAIN ==================
    public FullInheritanceResponse calculateProblem(InheritanceCalculationRequest request) {

        validateRequest(request);

        InheritanceCase c = new InheritanceCase(
                request.totalEstate(),
                request.debts(),
                request.will(),
                request.heirs()
        );

        BigDecimal netEstate = c.getNetEstate();

        // ================== APPLY RULES ==================
        List<InheritanceShareDto> results = new ArrayList<>();

        for (InheritanceRule rule : rules) {
            if (rule.canApply(c)) {
                InheritanceShareDto dto = rule.calculate(c);
                if (dto != null) {
                    results.add(dto);
                }
            }
        }

        // ================== SPLIT ==================
        List<InheritanceShareDto> fixedShares = new ArrayList<>();
        List<InheritanceShareDto> allAsaba = new ArrayList<>();

        for (InheritanceShareDto dto : results) {
            if (dto.shareType() == ShareType.FIXED) {
                fixedShares.add(dto);
            } else if (dto.shareType() == ShareType.TAASIB ||
                    dto.shareType() == ShareType.MALE_DOUBLE_FEMALE ||
                    dto.shareType() == ShareType.MIXED) {
                allAsaba.add(dto);
            }
        }

        // ================== ORIGIN ==================
        int origin = calculateOrigin(fixedShares);
        if (origin <= 0) origin = 1;

        Map<HeirType, BigDecimal> sharesMap = new LinkedHashMap<>();
        Map<HeirType, InheritanceShareDto> dtoMap = new LinkedHashMap<>();

        // ================== DISTRIBUTE FIXED SHARES ==================
        for (InheritanceShareDto dto : fixedShares) {
            FixedShare fs = dto.fixedShare();
            if (fs == null) continue;

            BigDecimal units;

            if (fs == FixedShare.THIRD_OF_REMAINDER) {
                BigDecimal spouseUnits = BigDecimal.ZERO;

                for (InheritanceShareDto d : fixedShares) {
                    if (d.heirType().isSpouse()) {
                        FixedShare s = d.fixedShare();
                        spouseUnits = spouseUnits.add(
                                BigDecimal.valueOf(origin)
                                        .multiply(BigDecimal.valueOf(s.getNumerator()))
                                        .divide(BigDecimal.valueOf(s.getDenominator()), 10, RoundingMode.HALF_UP)
                        );
                    }
                }

                BigDecimal remainder = BigDecimal.valueOf(origin).subtract(spouseUnits);
                units = remainder.divide(BigDecimal.valueOf(3), 10, RoundingMode.HALF_UP);
            } else {
                units = BigDecimal.valueOf(origin)
                        .multiply(BigDecimal.valueOf(fs.getNumerator()))
                        .divide(BigDecimal.valueOf(fs.getDenominator()), 10, RoundingMode.HALF_UP);
            }

            sharesMap.put(dto.heirType(), units);
            dtoMap.put(dto.heirType(), dto);
        }

        // ================== AWL ON FIXED SHARES ==================
        applyAwlOnFixedShares(sharesMap, dtoMap, origin);

        // ================== DISTRIBUTE ASABA ==================
        distributeAllAsaba(c, allAsaba, sharesMap, dtoMap, origin);

        // ================== RAD (الرد) ==================
        applyRaddIfNoAsaba(c, allAsaba, sharesMap, dtoMap, origin);

        // ================== CONVERT TO MONEY ==================
        BigDecimal unitValue = netEstate.divide(BigDecimal.valueOf(origin), 10, RoundingMode.HALF_UP);

        List<InheritanceShareDto> finalShares = new ArrayList<>();

        for (Map.Entry<HeirType, BigDecimal> e : sharesMap.entrySet()) {
            HeirType type = e.getKey();
            BigDecimal units = e.getValue();
            InheritanceShareDto baseDto = dtoMap.get(type);
            int count = type.isSinglePerson() ? 1 : c.count(type);

            BigDecimal totalAmount = units.multiply(unitValue).setScale(2, RoundingMode.HALF_UP);
            double perPerson = totalAmount.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP).doubleValue();

            finalShares.add(baseDto.withAmounts(perPerson, totalAmount.doubleValue()));
        }

        // ================== ROUND ==================
        List<InheritanceShareDto> roundedShares = InheritanceUtils.roundShares(finalShares, netEstate.doubleValue());

        double totalDistributed = roundedShares.stream()
                .mapToDouble(InheritanceShareDto::totalAmount)
                .sum();
        double remainingEstate = netEstate.doubleValue() - totalDistributed;

        FullInheritanceResponse response = new FullInheritanceResponse(
                arabicInheritanceTextService.generateText(request),
                request.totalEstate().doubleValue(),
                netEstate.doubleValue(),
                roundedShares,
                remainingEstate
        );

        User currentUser = securityUtil.getCurrentUser();
        if (currentUser != null) {
            inheritanceProblemService.saveInheritanceProblem(response, currentUser);
        }

        return response;
    }

    // ================== DISTRIBUTE ASABA ==================
    private void distributeAllAsaba(
            InheritanceCase c,
            List<InheritanceShareDto> allAsaba,
            Map<HeirType, BigDecimal> sharesMap,
            Map<HeirType, InheritanceShareDto> dtoMap,
            int origin
    ) {
        if (allAsaba.isEmpty()) return;

        BigDecimal used = sharesMap.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainder = BigDecimal.valueOf(origin).subtract(used);

        if (remainder.compareTo(BigDecimal.ZERO) <= 0) return;

        // 1. فرز العصبات حسب الأولوية
        allAsaba.sort(Comparator.comparingInt(dto -> dto.heirType().getAsabaRank()));

        // 2. معالجة MIXED أولاً (مثل الأب مع بنت)
        for (InheritanceShareDto dto : allAsaba) {
            if (dto.shareType() == ShareType.MIXED) {
                HeirType type = dto.heirType();

                // يأخذ فرضه إذا لم يأخذه
                if (dto.fixedShare() != null && !sharesMap.containsKey(type)) {
                    FixedShare fs = dto.fixedShare();
                    BigDecimal fixedUnits = BigDecimal.valueOf(origin)
                            .multiply(BigDecimal.valueOf(fs.getNumerator()))
                            .divide(BigDecimal.valueOf(fs.getDenominator()), 10, RoundingMode.HALF_UP);

                    sharesMap.put(type, fixedUnits);
                    dtoMap.put(type, dto);

                    used = used.add(fixedUnits);
                    remainder = BigDecimal.valueOf(origin).subtract(used);
                }

                // يأخذ الباقي
                if (remainder.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal current = sharesMap.getOrDefault(type, BigDecimal.ZERO);
                    sharesMap.put(type, current.add(remainder));
                }
                return;
            }
        }

        // 3. MALE_DOUBLE_FEMALE
        boolean hasMaleDoubleFemale = allAsaba.stream()
                .anyMatch(dto -> dto.shareType() == ShareType.MALE_DOUBLE_FEMALE);

        if (hasMaleDoubleFemale) {
            List<InheritanceShareDto> group = allAsaba.stream()
                    .filter(dto -> dto.shareType() == ShareType.MALE_DOUBLE_FEMALE)
                    .toList();

            int totalUnits = 0;
            Map<HeirType, Integer> unitsMap = new LinkedHashMap<>();

            for (InheritanceShareDto dto : group) {
                HeirType type = dto.heirType();
                int count = c.count(type);
                int units = count * type.getUnit();

                if (units > 0) {
                    unitsMap.put(type, units);
                    totalUnits += units;
                    dtoMap.put(type, dto);
                }
            }

            if (totalUnits > 0) {
                BigDecimal unitValue = remainder.divide(BigDecimal.valueOf(totalUnits), 10, RoundingMode.HALF_UP);
                for (Map.Entry<HeirType, Integer> e : unitsMap.entrySet()) {
                    BigDecimal share = unitValue.multiply(BigDecimal.valueOf(e.getValue()));
                    sharesMap.put(e.getKey(), share);
                }
            }
            return;
        }

        // 4. TAASIB عادي
        if (!allAsaba.isEmpty()) {
            InheritanceShareDto strongest = allAsaba.get(0);
            HeirType type = strongest.heirType();
            int count = c.count(type);
            int units = count * type.getUnit();

            if (units > 0) {
                BigDecimal unitValue = remainder.divide(BigDecimal.valueOf(units), 10, RoundingMode.HALF_UP);
                BigDecimal share = unitValue.multiply(BigDecimal.valueOf(units));
                sharesMap.put(type, share);
                dtoMap.put(type, strongest);
            }
        }
    }

    // ================== AWL ON FIXED SHARES ==================
    private void applyAwlOnFixedShares(
            Map<HeirType, BigDecimal> sharesMap,
            Map<HeirType, InheritanceShareDto> dtoMap,
            int origin
    ) {
        BigDecimal originBD = BigDecimal.valueOf(origin);
        BigDecimal totalFixed = BigDecimal.ZERO;

        // حساب الفروض فقط
        for (Map.Entry<HeirType, BigDecimal> entry : sharesMap.entrySet()) {
            InheritanceShareDto dto = dtoMap.get(entry.getKey());
            if (dto != null && dto.shareType() == ShareType.FIXED) {
                totalFixed = totalFixed.add(entry.getValue());
            }
        }

        if (totalFixed.compareTo(originBD) > 0) {
            BigDecimal ratio = originBD.divide(totalFixed, 10, RoundingMode.HALF_UP);

            for (Map.Entry<HeirType, BigDecimal> entry : sharesMap.entrySet()) {
                InheritanceShareDto dto = dtoMap.get(entry.getKey());
                if (dto != null && dto.shareType() == ShareType.FIXED) {
                    BigDecimal newShare = entry.getValue().multiply(ratio);
                    sharesMap.put(entry.getKey(), newShare);
                }
            }
        }
    }

    // ================== RAD (الرد) ==================
    private void applyRaddIfNoAsaba(
            InheritanceCase c,
            List<InheritanceShareDto> allAsaba,
            Map<HeirType, BigDecimal> sharesMap,
            Map<HeirType, InheritanceShareDto> dtoMap,
            int origin
    ) {
        // لا رد مع وجود عصبة
        if (!allAsaba.isEmpty()) {
            return;
        }

        BigDecimal used = sharesMap.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainder = BigDecimal.valueOf(origin).subtract(used);

        if (remainder.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        // جمع أهل الرد (عدا الزوجين)
        BigDecimal totalEligible = BigDecimal.ZERO;
        Map<HeirType, BigDecimal> eligibleShares = new LinkedHashMap<>();

        for (Map.Entry<HeirType, BigDecimal> entry : sharesMap.entrySet()) {
            HeirType type = entry.getKey();
            if (!type.isSpouse()) {
                totalEligible = totalEligible.add(entry.getValue());
                eligibleShares.put(type, entry.getValue());
            }
        }

        // إذا كان الزوج/الزوجة وحده
        if (totalEligible.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal sharePerHeir = remainder.divide(
                    BigDecimal.valueOf(sharesMap.size()), 10, RoundingMode.HALF_UP);

            for (HeirType type : sharesMap.keySet()) {
                BigDecimal current = sharesMap.get(type);
                sharesMap.put(type, current.add(sharePerHeir));
            }
            return;
        }

        // توزيع الرد
        for (Map.Entry<HeirType, BigDecimal> entry : eligibleShares.entrySet()) {
            BigDecimal ratio = entry.getValue().divide(totalEligible, 10, RoundingMode.HALF_UP);
            BigDecimal additional = remainder.multiply(ratio);
            BigDecimal current = sharesMap.getOrDefault(entry.getKey(), BigDecimal.ZERO);
            sharesMap.put(entry.getKey(), current.add(additional));
        }
    }

    // ================== ORIGIN ==================
    private int calculateOrigin(List<InheritanceShareDto> shares) {
        return shares.stream()
                .map(InheritanceShareDto::fixedShare)
                .filter(Objects::nonNull)
                .map(FixedShare::getDenominator)
                .reduce(this::lcm)
                .orElse(1);
    }

    private int lcm(int a, int b) {
        return a * (b / gcd(a, b));
    }

    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    // ================== VALIDATION ==================
    private void validateRequest(InheritanceCalculationRequest request) {
        if (request == null)
            throw new InvalidInheritanceCaseException("Request must not be null");
        if (request.heirs() == null || request.heirs().isEmpty())
            throw new InvalidInheritanceCaseException("Heirs must not be empty");
        if (request.totalEstate().compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidInheritanceCaseException("Estate must be positive");
    }
}