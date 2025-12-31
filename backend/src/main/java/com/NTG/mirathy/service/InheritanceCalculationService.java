package com.NTG.mirathy.service;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.DTOs.request.InheritanceCalculationRequest;
import com.NTG.mirathy.DTOs.response.FullInheritanceResponse;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.exceptionHandler.InvalidInheritanceCaseException;
import com.NTG.mirathy.rule.InheritanceRule;
import com.NTG.mirathy.util.InheritanceCase;
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

    public FullInheritanceResponse calculateProblem(InheritanceCalculationRequest request) {

        validateRequest(request);

        InheritanceCase c = new InheritanceCase(
                request.totalEstate(),
                request.debts(),
                request.will(),
                request.heirs()
        );

        BigDecimal netEstate = c.getNetEstate();

        /* =========================
           1️⃣ تطبيق القواعد
        ========================= */
        List<InheritanceShareDto> allShares = new ArrayList<>();
        for (InheritanceRule rule : rules) {
            if (rule.canApply(c)) {
                InheritanceShareDto dto = rule.calculate(c);
                if (dto != null) {
                    allShares.add(dto.withCount(c.count(dto.heirType())));
                }
            }
        }

        /* =========================
           2️⃣ فصل الفروض عن العصبات
        ========================= */
        List<InheritanceShareDto> fixedShares = new ArrayList<>();
        List<InheritanceShareDto> asabaShares = new ArrayList<>();

        for (InheritanceShareDto dto : allShares) {
            if (dto.shareType() == ShareType.FIXED) {
                fixedShares.add(dto);
            } else if (dto.shareType() == ShareType.TAASIB) {
                asabaShares.add(dto);
            }
        }

        /* =========================
           3️⃣ حساب أصل المسألة
        ========================= */
        int origin = calculateOrigin(fixedShares);

        Map<HeirType, InheritanceShareDto> dtoMap = new LinkedHashMap<>();
        Map<HeirType, BigDecimal> sharesMap = new LinkedHashMap<>();
        Map<HeirType, Integer> countMap = new LinkedHashMap<>();

        /* =========================
           4️⃣ توزيع الفروض
        ========================= */
        for (InheritanceShareDto dto : fixedShares) {
            if (dto.fixedShare() == null || dto.count() == 0) continue;

            FixedShare fs = dto.fixedShare();

            BigDecimal shareUnits =
                    BigDecimal.valueOf(origin)
                            .multiply(BigDecimal.valueOf(fs.getNumerator()))
                            .divide(BigDecimal.valueOf(fs.getDenominator()), 10, RoundingMode.HALF_UP);

            dtoMap.put(dto.heirType(), dto);
            sharesMap.put(dto.heirType(), shareUnits);
            countMap.put(dto.heirType(), dto.count());
        }

        /* =========================
           5️⃣ حساب الباقي
        ========================= */
        BigDecimal fixedSum = sharesMap.values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remaining = BigDecimal.valueOf(origin).subtract(fixedSum);

        /* =========================
           6️⃣ توزيع التعصيب (عام)
        ========================= */
        if (remaining.compareTo(BigDecimal.ZERO) > 0 && !asabaShares.isEmpty()) {
            distributeAsaba(c, asabaShares, dtoMap, countMap, sharesMap, remaining);
        }

        /* =========================
           7️⃣ العول أو الرد
        ========================= */
        applyAwlAndRadd(sharesMap, dtoMap, origin);

        /* =========================
           8️⃣ تحويل الأسهم لمبالغ
        ========================= */
        BigDecimal shareValue =
                netEstate.divide(BigDecimal.valueOf(origin), 10, RoundingMode.HALF_UP);

        List<InheritanceShareDto> finalShares = new ArrayList<>();

        for (HeirType type : dtoMap.keySet()) {
            BigDecimal totalAmount =
                    sharesMap.get(type)
                            .multiply(shareValue)
                            .setScale(2, RoundingMode.HALF_UP);

            int count = countMap.get(type);

            double amountPerPerson =
                    totalAmount.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP)
                            .doubleValue();

            finalShares.add(
                    dtoMap.get(type).withAmounts(amountPerPerson, totalAmount.doubleValue())
            );
        }

        return new FullInheritanceResponse(
                arabicInheritanceTextService.generateText(request),
                request.totalEstate().doubleValue(),
                netEstate.doubleValue(),
                finalShares,
                0.0
        );
    }

    /* ============================================================
       توزيع العصبات — قاعدة واحدة لكل:
       ابن / بنت
       أخ / أخت
       ابن ابن / بنت ابن
    ============================================================ */
    private void distributeAsaba(
            InheritanceCase c,
            List<InheritanceShareDto> asabaShares,
            Map<HeirType, InheritanceShareDto> dtoMap,
            Map<HeirType, Integer> countMap,
            Map<HeirType, BigDecimal> sharesMap,
            BigDecimal remaining
    ) {

        int totalUnits = 0;
        Map<HeirType, Integer> unitsMap = new LinkedHashMap<>();

        for (InheritanceShareDto dto : asabaShares) {
            HeirType type = dto.heirType();
            int count = c.count(type);

            if (count > 0 && type.isTaasib()) {
                int units = count * type.getUnit(); // ⭐ الذكر 2 – الأنثى 1
                unitsMap.put(type, units);
                totalUnits += units;
            }
        }

        if (totalUnits == 0) return;

        BigDecimal unitValue =
                remaining.divide(BigDecimal.valueOf(totalUnits), 10, RoundingMode.HALF_UP);

        for (Map.Entry<HeirType, Integer> entry : unitsMap.entrySet()) {
            HeirType type = entry.getKey();
            BigDecimal totalShare =
                    unitValue.multiply(BigDecimal.valueOf(entry.getValue()));

            dtoMap.put(
                    type,
                    asabaShares.stream()
                            .filter(d -> d.heirType() == type)
                            .findFirst()
                            .orElseThrow()
            );

            sharesMap.put(type, totalShare);
            countMap.put(type, c.count(type));
        }
    }

    /* =========================
       العول والرد
    ========================= */
    private void applyAwlAndRadd(
            Map<HeirType, BigDecimal> sharesMap,
            Map<HeirType, InheritanceShareDto> dtoMap,
            int origin
    ) {

        BigDecimal total = sharesMap.values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal originBD = BigDecimal.valueOf(origin);

        // 🔺 العول
        if (total.compareTo(originBD) > 0) {
            for (HeirType type : sharesMap.keySet()) {
                BigDecimal adjusted =
                        sharesMap.get(type)
                                .multiply(originBD)
                                .divide(total, 10, RoundingMode.HALF_UP);
                sharesMap.put(type, adjusted);
            }
        }

        // 🔻 الرد
        else if (total.compareTo(originBD) < 0) {

            BigDecimal remaining = originBD.subtract(total);
            BigDecimal fixedTotal = BigDecimal.ZERO;

            for (HeirType type : sharesMap.keySet()) {
                if (dtoMap.get(type).shareType() == ShareType.FIXED) {
                    fixedTotal = fixedTotal.add(sharesMap.get(type));
                }
            }

            if (fixedTotal.compareTo(BigDecimal.ZERO) > 0) {
                for (HeirType type : sharesMap.keySet()) {
                    if (dtoMap.get(type).shareType() == ShareType.FIXED) {
                        BigDecimal current = sharesMap.get(type);
                        BigDecimal fraction =
                                current.divide(fixedTotal, 10, RoundingMode.HALF_UP);
                        sharesMap.put(type, current.add(remaining.multiply(fraction)));
                    }
                }
            }
        }
    }

    /* ========================= */
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

    private void validateRequest(InheritanceCalculationRequest request) {
        if (request == null)
            throw new InvalidInheritanceCaseException("Request must not be null");
        if (request.heirs() == null || request.heirs().isEmpty())
            throw new InvalidInheritanceCaseException("Heirs must not be empty");
        if (request.totalEstate().compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidInheritanceCaseException("Estate must be positive");
    }
}
