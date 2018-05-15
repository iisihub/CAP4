package com.iisigroup.colabase.loan.model;

import java.math.BigDecimal;

public class LoanDetail {
    // 期數
    private int no;
    // 當期本金
    private BigDecimal principal;
    // 當期利息
    private BigDecimal interest;
    // 當期分期金額
    private BigDecimal installment;
    // 當期餘額
    private BigDecimal balance;
    // 當期費用(支出)
    private BigDecimal expense;

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getInstallment() {
        return installment;
    }

    public void setInstallment(BigDecimal installment) {
        this.installment = installment;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getExpense() {
        return expense;
    }

    public void setExpense(BigDecimal expense) {
        this.expense = expense;
    }
}
