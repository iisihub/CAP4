package com.iisigroup.colabase.loan.service;

import java.math.BigDecimal;

import com.iisigroup.colabase.loan.model.LoanInfo;


public interface LoanService {
    /**
     * 固定借款利率的總費用年率計算。
     * 
     * @param amount
     *            申請金額
     * @param tenor
     *            貸款期數
     * @param eppRate
     *            借款利率
     * @param upfrontFee
     *            手續費
     * @return
     */
    LoanInfo calFixedFee(BigDecimal amount, int tenor, BigDecimal eppRate, BigDecimal upfrontFee);

    /**
     * 兩階借款利率的總費用年率計算。
     * 
     * @param amount
     *            申請金額
     * @param tenor
     *            貸款期數
     * @param tenorTier1
     *            第一階期數
     * @param eppRateTier1
     *            第一階利率
     * @param eppRateTier2
     *            第二階利率
     * @param upfrontFee
     *            手續費
     * @return
     */
    LoanInfo calTwoTierFee(BigDecimal amount, int tenor, int tenorTier1, BigDecimal eppRateTier1, BigDecimal eppRateTier2, BigDecimal upfrontFee);
}
