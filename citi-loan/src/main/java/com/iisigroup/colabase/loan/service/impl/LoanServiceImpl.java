package com.iisigroup.colabase.loan.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.iisigroup.colabase.loan.model.LoanDetail;
import com.iisigroup.colabase.loan.model.LoanInfo;
import com.iisigroup.colabase.loan.service.LoanService;


@Service
public class LoanServiceImpl implements LoanService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final static int SCALE = 50;

    public LoanInfo calFixedFee(BigDecimal amount, int tenor, BigDecimal eppRate, BigDecimal upfrontFee) {
        logger.debug("calFixedFee => " + "amount : " + amount.toString() + ", tenor : " + tenor + ", eppRate : " + eppRate.toString() + ", upfrontFee : " + upfrontFee.toString());
        LoanInfo loanInfo = new LoanInfo();
        loanInfo.setAmount(amount);
        loanInfo.setFirstEppRate(eppRate);
        loanInfo.setTenor(tenor);
        loanInfo.setUpfrontFee(upfrontFee);
        // 月利率
        BigDecimal monthRate = eppRate.setScale(SCALE, BigDecimal.ROUND_HALF_UP).divide(BigDecimal.valueOf(12), BigDecimal.ROUND_HALF_UP);
        // 預設每月分期金額
        BigDecimal defaultInstallment = calDefaultInstallment(amount, tenor, monthRate, 0);
        logger.debug("INSTMENT:       " + calDefaultInstallment(amount, tenor, monthRate, 2));
        logger.debug("P+I adjustment: " + defaultInstallment);
        List<LoanDetail> details = new ArrayList<LoanDetail>();
        logger.debug("\n##\tPRINCIPAL\tINTEREST\tINSTALLMENT\tBALANCE\tEXPENSE");
        for (int i = 0; i <= tenor; i++) {
            LoanDetail detail = new LoanDetail();
            detail.setNo(i);
            if (i == 0) {
                // 第 0 期(期初)
                // 餘額為總貸款金額
                detail.setBalance(amount);
                // 費用為 -1*總貸款金額
                detail.setExpense(amount.negate());
            } else {
                // 從第 1 期開始...
                // 利息為 前期餘額*月利率、四捨五入到整數位
                detail.setInterest(details.get(i - 1).getBalance().multiply(monthRate).setScale(0, BigDecimal.ROUND_HALF_UP).setScale(2));
                if (i == tenor) {
                    // 若為最後一期
                    // 本期本金為 前期餘額
                    detail.setPrincipal(details.get(i - 1).getBalance().setScale(2));
                    // 本期分期金額為 本期本金+本期利息
                    detail.setInstallment(detail.getPrincipal().add(detail.getInterest()).setScale(2));
                } else {
                    // 若不是最後一期
                    // 本期分期金額為 預設每月分期金額
                    detail.setInstallment(defaultInstallment.setScale(2));
                    // 本期本金為 本期分期金額-本期利息
                    detail.setPrincipal(detail.getInstallment().subtract(detail.getInterest()).setScale(2));
                }
                // 本期餘額為 前期餘額-本期分期金額+本期利息
                detail.setBalance(details.get(i - 1).getBalance().subtract(detail.getInstallment()).add(detail.getInterest()).setScale(2));
                // 若期數==1，本期支出為 預設每月分期金額+手續費
                // 若期數!=1，本期支出為 本期分期金額
                detail.setExpense(i == 1 ? detail.getInstallment().add(upfrontFee).setScale(2) : detail.getInstallment().setScale(2));
            }
            logger.debug(i + "\t" + detail.getPrincipal() + "\t" + detail.getInterest() + "\t" + detail.getInstallment() + "\t" + detail.getBalance() + "\t" + detail.getExpense());
            details.add(i, detail);
        }
        // 2015.05.04 第一期月付金不含手續費
        loanInfo.setFirstExpense(details.get(1).getExpense().subtract(upfrontFee));
        loanInfo.setLastExpense(details.get(tenor).getExpense());
        long now = System.currentTimeMillis();
        BigDecimal apr = calAPR(details, monthRate).multiply(BigDecimal.valueOf(12));
        logger.debug("\rapr       : " + apr.multiply(BigDecimal.valueOf(100)).setScale(4, BigDecimal.ROUND_HALF_UP) + "%");
        logger.debug("cost      : " + (System.currentTimeMillis() - now));
        // now = System.currentTimeMillis();
        // apr = calAPR1(details, monthRate).multiply(BigDecimal.valueOf(12));
        // logger.debug("\rapr       : "
        // + apr.multiply(BigDecimal.valueOf(100)).setScale(4,
        // BigDecimal.ROUND_HALF_UP) + "%");
        // logger.debug("cost      : " + (System.currentTimeMillis() -
        // now));
        loanInfo.setApr(apr);
        return loanInfo;
    }

    public LoanInfo calTwoTierFee(BigDecimal amount, int tenor, int firstTenor, BigDecimal firstEppRate, BigDecimal secondEppRate, BigDecimal upfrontFee) {
        logger.debug("calTwoTierFee=> ");
        logger.debug("amount: " + amount.toString() + ", tenor: " + tenor + ", firstTenor: " + firstTenor + ", firstEppRate: " + firstEppRate.toString() + ", secondEppRate: "
                + secondEppRate.toString() + ", upfrontFee: " + upfrontFee.toString());
        LoanInfo loanInfo = new LoanInfo();
        loanInfo.setAmount(amount);
        loanInfo.setFirstEppRate(firstEppRate);
        loanInfo.setSecondEppRate(secondEppRate);
        loanInfo.setTenor(tenor);
        loanInfo.setUpfrontFee(upfrontFee);
        // 月利率
        BigDecimal firstMonthRate = firstEppRate.setScale(SCALE, BigDecimal.ROUND_HALF_UP).divide(BigDecimal.valueOf(12), BigDecimal.ROUND_HALF_UP);
        BigDecimal secondMonthRate = secondEppRate.setScale(SCALE, BigDecimal.ROUND_HALF_UP).divide(BigDecimal.valueOf(12), BigDecimal.ROUND_HALF_UP);
        // 預設每月分期金額
        BigDecimal defaultInstallment = calDefaultInstallment(amount, tenor, firstMonthRate, 0);
        logger.debug("INSTMENT:       " + calDefaultInstallment(amount, tenor, firstMonthRate, 2));
        logger.debug("P+I adjustment: " + defaultInstallment);
        BigDecimal installmentTier2 = null;
        List<LoanDetail> details = new ArrayList<LoanDetail>();
        logger.debug("\n##\tPRINCIPAL\tINTEREST\tINSTALLMENT\tBALANCE\tEXPENSE");
        for (int i = 0; i <= tenor; i++) {
            LoanDetail detail = new LoanDetail();
            detail.setNo(i);
            if (i == 0) {
                // 第 0 期(期初)
                // 餘額為總貸款金額
                detail.setBalance(amount);
                // 費用為 -1*總貸款金額
                detail.setExpense(amount.negate());
            } else {
                // 從第 1 期開始...
                // 利息為 前期餘額*月利率、四捨五入到整數位
                if (i <= firstTenor) {
                    detail.setInterest(details.get(i - 1).getBalance().multiply(firstMonthRate).setScale(0, BigDecimal.ROUND_HALF_UP).setScale(2));
                } else {
                    detail.setInterest(details.get(i - 1).getBalance().multiply(secondMonthRate).setScale(0, BigDecimal.ROUND_HALF_UP).setScale(2));
                }
                if (i == tenor) {
                    // 若為最後一期
                    // 本期本金為 前期餘額
                    detail.setPrincipal(details.get(i - 1).getBalance().setScale(2));
                    // 本期分期金額為 本期本金+本期利息
                    detail.setInstallment(detail.getPrincipal().add(detail.getInterest()).setScale(2));
                } else {
                    // 若不是最後一期
                    if (i <= firstTenor) {
                        // 本期分期金額為 預設每月分期金額
                        detail.setInstallment(defaultInstallment.setScale(2));
                    } else {
                        if (i == firstTenor + 1) {
                            // tier2 開始
                            installmentTier2 = calDefaultInstallment(details.get(i - 1).getBalance(), tenor - firstTenor, secondMonthRate, 0);
                            logger.debug("INSTMENT2:      " + calDefaultInstallment(details.get(i - 1).getBalance(), tenor - firstTenor, secondMonthRate, 2));
                            logger.debug("P+I adjustment2:" + installmentTier2);
                        }
                        detail.setInstallment(installmentTier2);
                    }
                    // 本期本金為 本期分期金額-本期利息
                    detail.setPrincipal(detail.getInstallment().subtract(detail.getInterest()).setScale(2));
                }
                // 本期餘額為 前期餘額-本期分期金額+本期利息
                detail.setBalance(details.get(i - 1).getBalance().subtract(detail.getInstallment()).add(detail.getInterest()).setScale(2));
                // 若期數==1，本期支出為 預設每月分期金額+手續費
                // 若期數!=1，本期支出為 本期分期金額
                detail.setExpense(i == 1 ? detail.getInstallment().add(upfrontFee).setScale(2) : detail.getInstallment().setScale(2));
            }
            logger.debug(i + "\t" + detail.getPrincipal() + "\t" + detail.getInterest() + "\t" + detail.getInstallment() + "\t" + detail.getBalance() + "\t" + detail.getExpense());
            details.add(i, detail);
        }
        // 2015.05.04 第一期月付金不含手續費
        loanInfo.setFirstExpense(details.get(1).getExpense().subtract(upfrontFee));
        loanInfo.setTier2Expense(details.get(firstTenor + 1).getExpense());
        loanInfo.setLastExpense(details.get(tenor).getExpense());
        long now = System.currentTimeMillis();
        BigDecimal apr = calAPR(details, firstMonthRate).multiply(BigDecimal.valueOf(12));
        logger.debug("\rapr       : " + apr.multiply(BigDecimal.valueOf(100)).setScale(4, BigDecimal.ROUND_HALF_UP) + "%");
        logger.debug("cost      : " + (System.currentTimeMillis() - now));
        // now = System.currentTimeMillis();
        // apr = calAPR1(details, monthRate).multiply(BigDecimal.valueOf(12));
        // logger.debug("\rapr       : "
        // + apr.multiply(BigDecimal.valueOf(100)).setScale(4,
        // BigDecimal.ROUND_HALF_UP) + "%");
        // logger.debug("cost      : " + (System.currentTimeMillis() -
        // now));
        loanInfo.setApr(apr);
        return loanInfo;
    }

    private BigDecimal calDefaultInstallment(BigDecimal amount, int tenor, BigDecimal monthRate, int scale) {
        BigDecimal installment = null;
        if (monthRate.compareTo(BigDecimal.ZERO) > 0) {
            // installment = amount*(1+r)^n*r/[(1+r)^n-1]
            installment = amount.multiply(monthRate.add(BigDecimal.ONE).pow(tenor)).multiply(monthRate).setScale(SCALE, BigDecimal.ROUND_HALF_UP)
                    .divide(monthRate.add(BigDecimal.ONE).pow(tenor).subtract(BigDecimal.ONE), BigDecimal.ROUND_HALF_UP);
        } else {
            installment = amount.setScale(SCALE, BigDecimal.ROUND_HALF_UP).divide(BigDecimal.valueOf(tenor), BigDecimal.ROUND_HALF_UP);
        }
        // 2015.05.04 比照 Excel，先四捨五入到小數點第二位
        installment = installment.setScale(2, BigDecimal.ROUND_HALF_UP);
        return installment.setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * <pre>
     * 參考 apache poi irr，using the Newton-Raphson Method.
     * 但是沒有去推敲 derivative 的公式怎麼來的。
     * http://svn.apache.org/repos/asf/poi/trunk/src/java/org/apache/poi/ss/formula/functions/Irr.java
     * </pre>
     * 
     * @param details
     * @param guess
     * @return
     */
    private BigDecimal calAPR(List<LoanDetail> details, BigDecimal guess) {
        double absoluteAccuracy = 1E-15;
        BigDecimal apr = BigDecimal.ZERO;
        while (true) {
            BigDecimal npv = BigDecimal.ZERO;
            BigDecimal derivative = BigDecimal.ZERO;
            for (LoanDetail detail : details) {
                // npv += En / (1+guess)^n
                npv = npv.add(detail.getExpense().setScale(SCALE, BigDecimal.ROUND_HALF_UP).divide(guess.add(BigDecimal.ONE).pow(detail.getNo()), BigDecimal.ROUND_HALF_UP));
                // derivative += En / (1+guess)^(n+1) * (-n)
                derivative = derivative.add(detail.getExpense().setScale(SCALE, BigDecimal.ROUND_HALF_UP).divide(guess.add(BigDecimal.ONE).pow(detail.getNo() + 1), BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(detail.getNo())).negate());
            }
            // apr = guess - npv / derivative
            apr = guess.subtract(npv.setScale(SCALE, BigDecimal.ROUND_HALF_UP).divide(derivative, BigDecimal.ROUND_HALF_UP));
            logger.debug("\rguess     : " + guess.toString());
            logger.debug("npv       : " + npv);
            logger.debug("derivative: " + derivative);
            logger.debug("apr'      : " + apr + "'");
            if (apr.subtract(guess).abs().doubleValue() > absoluteAccuracy) {
                guess = apr;
            } else {
                break;
            }
        }
        return apr;
    }

    /**
     * <pre>
     * Numerical solution (插補法)計算 IRR。
     * http://en.wikipedia.org/wiki/Internal_rate_of_return#Numerical_solution
     * </pre>
     * 
     * @param details
     * @param guess
     * @return
     */
    @SuppressWarnings("unused")
    private BigDecimal calAPR1(List<LoanDetail> details, BigDecimal guess) {
        double absoluteAccuracy = 1E-15;
        BigDecimal apr = BigDecimal.ZERO;
        BigDecimal _guess = BigDecimal.ZERO;
        BigDecimal _npv = BigDecimal.ZERO;
        int i = 0;
        while (true) {
            BigDecimal npv = BigDecimal.ZERO;
            for (LoanDetail detail : details) {
                // npv += En / (1+guess)^n
                npv = npv.add(detail.getExpense().setScale(SCALE).divide(guess.add(BigDecimal.ONE).pow(detail.getNo()), BigDecimal.ROUND_HALF_UP));
            }
            if (i != 0) {
                apr = guess.subtract(npv.multiply(guess.subtract(_guess).setScale(SCALE).divide(npv.subtract(_npv), BigDecimal.ROUND_HALF_UP)));
                logger.debug("\rguess     : " + guess.toString());
                logger.debug("npv       : " + npv);
                logger.debug("apr'      : " + apr + "'");
                if (apr.subtract(guess).abs().doubleValue() > absoluteAccuracy) {
                    _guess = guess;
                    _npv = npv;
                    guess = apr;
                } else {
                    break;
                }
            }
            i++;
        }
        return apr;
    }

    public static void main(String[] args) {
        LoanService s = new LoanServiceImpl();
        s.calFixedFee(BigDecimal.valueOf(1200000.00), 48, BigDecimal.valueOf(0.0399), BigDecimal.valueOf(5999));
        s.calTwoTierFee(BigDecimal.valueOf(80000.00), 24, 3, BigDecimal.valueOf(0.0699), BigDecimal.valueOf(0.0699), BigDecimal.valueOf(5999));
    }
}
