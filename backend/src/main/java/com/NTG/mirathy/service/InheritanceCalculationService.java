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
        List<InheritanceShareDto> fixed = new ArrayList<>();
        List<InheritanceShareDto> asaba = new ArrayList<>();

        for (InheritanceShareDto dto : results) {
            if (dto.shareType() == ShareType.FIXED || dto.shareType() == ShareType.MIXED) {
                fixed.add(dto); // MIXED هنا فرض فقط
            }
            if (dto.shareType() == ShareType.TAASIB) {
                asaba.add(dto);
            }
        }

        // ================== ORIGIN ==================
        int origin = calculateOrigin(fixed);
        if (origin <= 0) origin = 1;

        Map<HeirType, InheritanceShareDto> dtoMap = new LinkedHashMap<>();
        Map<HeirType, BigDecimal> sharesMap = new LinkedHashMap<>();

        // ================== DISTRIBUTE FIXED ==================
        for (InheritanceShareDto dto : fixed) {

            FixedShare fs = dto.fixedShare();
            if (fs == null) continue;

            BigDecimal units;

            // العمريّة (ثلث الباقي)
            if (fs == FixedShare.THIRD_OF_REMAINDER) {

                BigDecimal spouseUnits = BigDecimal.ZERO;

                for (InheritanceShareDto d : fixed) {
                    if (d.heirType().isSpouse()) {
                        FixedShare s = d.fixedShare();
                        spouseUnits = spouseUnits.add(
                                BigDecimal.valueOf(origin)
                                        .multiply(BigDecimal.valueOf(s.getNumerator()))
                                        .divide(BigDecimal.valueOf(s.getDenominator()), 10, RoundingMode.HALF_UP)
                        );
                    }
                }

                BigDecimal remainder =
                        BigDecimal.valueOf(origin).subtract(spouseUnits);

                units = remainder
                        .divide(BigDecimal.valueOf(3), 10, RoundingMode.HALF_UP);

            } else {
                units = BigDecimal.valueOf(origin)
                        .multiply(BigDecimal.valueOf(fs.getNumerator()))
                        .divide(BigDecimal.valueOf(fs.getDenominator()), 10, RoundingMode.HALF_UP);
            }

            dtoMap.put(dto.heirType(), dto);
            sharesMap.put(dto.heirType(), units);
        }

        // ================== AWL & RADD ==================
            boolean hasAsaba = !asaba.isEmpty();
            boolean appliedAwl = applyAwlAndRadd(sharesMap, dtoMap, origin, hasAsaba);
        // ================== ASABA (ONLY IF NO AWL) ==================

            distributeAsaba(c, asaba, sharesMap, dtoMap, origin);

        // ================== CONVERT TO MONEY ==================
        BigDecimal unitValue =
                netEstate.divide(BigDecimal.valueOf(origin), 10, RoundingMode.HALF_UP);

        List<InheritanceShareDto> finalShares = new ArrayList<>();

        for (Map.Entry<HeirType, InheritanceShareDto> e : dtoMap.entrySet()) {

            HeirType type = e.getKey();
            BigDecimal units = sharesMap.get(type);

            BigDecimal totalAmount =
                    units.multiply(unitValue).setScale(2, RoundingMode.HALF_UP);

            int count = type.isSinglePerson() ? 1 : c.count(type);

            double perPerson =
                    totalAmount
                            .divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP)
                            .doubleValue();

            finalShares.add(
                    e.getValue().withAmounts(perPerson, totalAmount.doubleValue())
            );
        }

        List<InheritanceShareDto> roundedShares = InheritanceUtils.roundShares(finalShares , netEstate.doubleValue());

        FullInheritanceResponse response= new FullInheritanceResponse(
                arabicInheritanceTextService.generateText(request),
                request.totalEstate().doubleValue(),
                netEstate.doubleValue(),
                roundedShares,
                0.0
        );

        User currentUser = securityUtil.getCurrentUser();
        if (getCurrentUser()!=null) {
            inheritanceProblemService.saveInheritanceProblem(response,currentUser);
        }

        return response;
    }
    private User getCurrentUser() {
        return securityUtil.getCurrentUser();
    }

    // ================== ASABA ==================
    private void distributeAsaba(
            InheritanceCase c,
            List<InheritanceShareDto> asaba,
            Map<HeirType, BigDecimal> sharesMap,
            Map<HeirType, InheritanceShareDto> dtoMap,
            int origin
    ) {

        if (asaba.isEmpty()) return;

        BigDecimal used =
                sharesMap.values().stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainder =
                BigDecimal.valueOf(origin).subtract(used);

        if (remainder.compareTo(BigDecimal.ZERO) <= 0) return;

        int totalUnits = 0;
        Map<HeirType, Integer> unitsMap = new LinkedHashMap<>();

        for (InheritanceShareDto dto : asaba) {

            HeirType type = dto.heirType();
            int count = c.count(type);

            int units = count * type.getUnit();
            if (units > 0) {
                unitsMap.put(type, units);
                totalUnits += units;
                dtoMap.put(type, dto);
            }
        }

        if (totalUnits == 0) return;

        BigDecimal unitValue =
                remainder.divide(
                        BigDecimal.valueOf(totalUnits),
                        10,
                        RoundingMode.HALF_UP
                );

        for (Map.Entry<HeirType, Integer> e : unitsMap.entrySet()) {

            HeirType type = e.getKey();

            BigDecimal share =
                    unitValue.multiply(BigDecimal.valueOf(e.getValue()));

            BigDecimal current =
                    sharesMap.getOrDefault(type, BigDecimal.ZERO);

            sharesMap.put(type, current.add(share));
        }
    }

    // ================== AWL & RADD ==================
    private boolean applyAwlAndRadd(
            Map<HeirType, BigDecimal> sharesMap,
            Map<HeirType, InheritanceShareDto> dtoMap,
            int origin,
            boolean hasAsaba
    ) {

        BigDecimal originBD = BigDecimal.valueOf(origin);

        BigDecimal total =
                sharesMap.values().stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean appliedAwl = false;
        // ===== AWL =====
        if (total.compareTo(originBD) > 0) {
            appliedAwl = true;
            Map<HeirType, BigDecimal> originalRatios = new HashMap<>();
            for (HeirType type : sharesMap.keySet()) {
                originalRatios.put(
                        type,
                        sharesMap.get(type)
                                .multiply(originBD)
                                .divide(total, 10, RoundingMode.HALF_UP));

            }
            for (HeirType type : sharesMap.keySet()) {
                BigDecimal ratio = originalRatios.get(type);
                sharesMap.put(type, originBD.multiply(ratio));
            }

        }

        // ===== RADD =====
        if (total.compareTo(originBD) < 0) {
            // لا رد مع وجود عصبة
            if (hasAsaba) {
                return appliedAwl;
            }
            BigDecimal remainder = originBD.subtract(total);
            BigDecimal eligible = BigDecimal.ZERO;

            for (Map.Entry<HeirType, InheritanceShareDto> e : dtoMap.entrySet()) {
                if (e.getValue().shareType() == ShareType.FIXED
                        && !e.getKey().isSpouse()) {
                    eligible = eligible.add(sharesMap.get(e.getKey()));
                }
            }

            if (eligible.compareTo(BigDecimal.ZERO) == 0) return false;

            for (Map.Entry<HeirType, InheritanceShareDto> e : dtoMap.entrySet()) {

                HeirType type = e.getKey();
                InheritanceShareDto dto = e.getValue();

                if (dto.shareType() == ShareType.FIXED && !type.isSpouse()) {

                    BigDecimal current = sharesMap.get(type);
                    BigDecimal ratio =
                            current.divide(eligible, 10, RoundingMode.HALF_UP);

                    sharesMap.put(
                            type,
                            current.add(remainder.multiply(ratio))
                    );
                }
            }
        }
        return false;
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
