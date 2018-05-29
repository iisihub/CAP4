package com.iisigroup.colabase.loan.handler;

import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.colabase.loan.model.LoanInfo;
import com.iisigroup.colabase.loan.service.LoanService;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.impl.AjaxFormResult;

/**
 * <pre>
 * demo use handler
 * </pre>
 * 
 * @since 2018年4月2日
 * @author Roger
 * @version
 *          <ul>
 *          <li>2018年4月2日,skunk,new
 *          </ul>
 */
@Controller("democaculateloanhandler")
public class DemoLoanHandler extends MFormHandler {

    @Autowired
    private LoanService loanService;
    
    private static final String AMOUNT = "amount";
    private static final String TENOR = "tenor";

    public Result caculateLoan(Request params) {
        AjaxFormResult result = new AjaxFormResult();
        LoanInfo loanInfo;
        if ("oneTier".equals(params.get("tierChoose"))) {
            BigDecimal amount = new BigDecimal(params.get(AMOUNT));
            Integer tenor = Integer.parseInt(params.get(TENOR));
            BigDecimal eppRate = new BigDecimal(params.get("eppRate"));
            BigDecimal amountupfrontFee = new BigDecimal(params.get("amountupfrontFee"));
            // 一段式利率
            loanInfo = loanService.calFixedFee(amount, tenor, eppRate, amountupfrontFee);
            result.set(AMOUNT, loanInfo.getAmount());
            result.set("apr", loanInfo.getApr());
            result.set("firstEppRate", loanInfo.getFirstEppRate());
            result.set("firstExpense", loanInfo.getFirstExpense());
            result.set("lastExpense", loanInfo.getLastExpense());
            result.set("upfrontFee", loanInfo.getUpfrontFee());
            result.set(TENOR, loanInfo.getTenor());
        } else if ("twoTier".equals(params.get("tierChoose"))) {
            // 二段式利率
            BigDecimal amount = new BigDecimal(params.get(AMOUNT));
            Integer tenor = Integer.parseInt(params.get(TENOR));
            Integer tenorTier1 = Integer.parseInt(params.get("tenorTier1"));
            BigDecimal eppRateTier1 = new BigDecimal(params.get("eppRateTier1"));
            BigDecimal eppRateTier2 = new BigDecimal(params.get("eppRateTier2"));
            BigDecimal amountupfrontFee = new BigDecimal(params.get("amountupfrontFee"));
            loanInfo = loanService.calTwoTierFee(amount, tenor, tenorTier1, eppRateTier1, eppRateTier2, amountupfrontFee);
            result.set(AMOUNT, loanInfo.getAmount());
            result.set("apr", loanInfo.getApr());
            result.set("firstEppRate", loanInfo.getFirstEppRate());
            result.set("firstExpense", loanInfo.getFirstExpense());
            result.set("lastExpense", loanInfo.getLastExpense());
            result.set("secondEppRate", loanInfo.getSecondEppRate());
            result.set("tier2Expense", loanInfo.getTier2Expense());
            result.set("upfrontFee", loanInfo.getUpfrontFee());
            result.set(TENOR, loanInfo.getTenor());
        }
        return result;
    }
}
