package com.iisigroup.colabase.loan.service.impl;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Test;

import com.iisigroup.colabase.loan.model.LoanInfo;
import com.iisigroup.colabase.loan.service.impl.LoanServiceImpl;;

public class LoanServiceImplTest {
    
    static
    {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(new ConsoleAppender(
                   new PatternLayout("%-6r [%p] %c - %m%n")));
    }
    
    private final BigDecimal amount = new BigDecimal(100000);
    private final int tenor = 24;
    private final BigDecimal eppRate = new BigDecimal(0.8);
    private final BigDecimal upfrontFee = new BigDecimal(2000);
    
    private final int firstTenor = 8;
    private final BigDecimal firstEppRate = new BigDecimal(1);
    private final BigDecimal secondEppRate = new BigDecimal(1.2);
    
    
    
    @Test
    public void testCalFixedFee() {
        System.out.println("--------------------------OneTier--------------------------");
        LoanServiceImpl loanServiceImpl = new LoanServiceImpl();
        LoanInfo loanInfo = loanServiceImpl.calFixedFee(amount, tenor, eppRate, upfrontFee);
        assertNotNull("Amount must not be null", loanInfo.getAmount());
        System.out.println("Amount : " + loanInfo.getAmount());
        assertNotNull("Apr must not be null", loanInfo.getApr());
        System.out.println("Apr : " + loanInfo.getApr());
        assertNotNull("FirstEppRate must not be null", loanInfo.getFirstEppRate());
        System.out.println("FirstEppRate : " + loanInfo.getFirstEppRate());
        assertNotNull("FirstExpense must not be null", loanInfo.getFirstExpense());
        System.out.println("FirstExpense : " + loanInfo.getFirstExpense());
        assertNotNull("LastExpense must not be null", loanInfo.getLastExpense());
        System.out.println("LastExpense : " + loanInfo.getLastExpense());
        assertNotNull("UpfrontFee must not be null", loanInfo.getUpfrontFee());
        System.out.println("UpfrontFee : " + loanInfo.getUpfrontFee());
        assertNotNull("Tenor must not be null", loanInfo.getTenor());
        System.out.println("Tenor : " + loanInfo.getTenor());
    }
    
    @Test
    public void testCalTwoTierFee() {
        System.out.println("--------------------------TwoTier--------------------------");
        LoanServiceImpl loanServiceImpl = new LoanServiceImpl();
        LoanInfo loanInfo = loanServiceImpl.calTwoTierFee(amount, tenor, firstTenor, firstEppRate, secondEppRate, upfrontFee);
        assertNotNull("Amount must not be null", loanInfo.getAmount());
        System.out.println("Amount : " + loanInfo.getAmount());
        assertNotNull("Apr must not be null", loanInfo.getApr());
        System.out.println("Apr : " + loanInfo.getApr());
        assertNotNull("FirstEppRate must not be null", loanInfo.getFirstEppRate());
        System.out.println("FirstEppRate : " + loanInfo.getFirstEppRate());
        assertNotNull("FirstExpense must not be null", loanInfo.getFirstExpense());
        System.out.println("FirstExpense : " + loanInfo.getFirstExpense());
        assertNotNull("LastExpense must not be null", loanInfo.getLastExpense());
        System.out.println("LastExpense : " + loanInfo.getLastExpense());
        assertNotNull("SecondEppRate must not be null", loanInfo.getSecondEppRate());
        System.out.println("SecondEppRate : " + loanInfo.getSecondEppRate());
        assertNotNull("Tier2Expense must not be null", loanInfo.getTier2Expense());
        System.out.println("Tier2Expense : " + loanInfo.getTier2Expense());
        assertNotNull("UpfrontFee must not be null", loanInfo.getUpfrontFee());
        System.out.println("UpfrontFee : " + loanInfo.getUpfrontFee());
        assertNotNull("Tenor must not be null", loanInfo.getTenor());
        System.out.println("Tenor : " + loanInfo.getTenor());
    }
    
}
