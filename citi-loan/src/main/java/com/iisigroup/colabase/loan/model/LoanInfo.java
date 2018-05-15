package com.iisigroup.colabase.loan.model;

import java.math.BigDecimal;
import java.util.List;

public class LoanInfo {
    // 貸款金額
    private BigDecimal amount;
    // 貸款期數
    private int tenor;
    // 貸款利率(第一階)
    private BigDecimal firstEppRate;
    // 貸款利率(第二階)
    private BigDecimal secondEppRate;
    // 手續費
    private BigDecimal upfrontFee;
    // 總費用年百分率
    private BigDecimal apr;
    // 第一期支付金額
    private BigDecimal firstExpense;
    // 第二階段支付金額
    private BigDecimal tier2Expense;
    // 最後一期支付金額
    private BigDecimal lastExpense;
    // 每期金額明細
    private List<LoanDetail> details;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getTenor() {
        return tenor;
    }

    public void setTenor(int tenor) {
        this.tenor = tenor;
    }

    public BigDecimal getFirstEppRate() {
        return firstEppRate;
    }

    public void setFirstEppRate(BigDecimal firstEppRate) {
        this.firstEppRate = firstEppRate;
    }

    public BigDecimal getUpfrontFee() {
        return upfrontFee;
    }

    public void setUpfrontFee(BigDecimal upfrontFee) {
        this.upfrontFee = upfrontFee;
    }

    public BigDecimal getApr() {
        return apr;
    }

    public void setApr(BigDecimal apr) {
        this.apr = apr;
    }

    public BigDecimal getFirstExpense() {
        return firstExpense;
    }

    public void setFirstExpense(BigDecimal firstExpense) {
        this.firstExpense = firstExpense;
    }

    public BigDecimal getLastExpense() {
        return lastExpense;
    }

    public void setLastExpense(BigDecimal lastExpense) {
        this.lastExpense = lastExpense;
    }

    public List<LoanDetail> getDetails() {
        return details;
    }

    public void setDetails(List<LoanDetail> details) {
        this.details = details;
    }

    public BigDecimal getSecondEppRate() {
        return secondEppRate;
    }

    public void setSecondEppRate(BigDecimal secondEppRate) {
        this.secondEppRate = secondEppRate;
    }

    public BigDecimal getTier2Expense() {
        return tier2Expense;
    }

    public void setTier2Expense(BigDecimal tier2Expense) {
        this.tier2Expense = tier2Expense;
    }
}
