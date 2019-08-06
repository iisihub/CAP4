package com.iisigroup.colabase.edm.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.varia.NullAppender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.iisigroup.cap.utils.CapSystemConfig;
import com.iisigroup.colabase.edm.model.EdmSetting;
import com.iisigroup.colabase.edm.service.EDMService;
import com.iisigroup.colabase.report.CCBasePageReport;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EDMServiceImplTest {

    final String edmFtlName = "edm1.ftl";
    final String edmFtlPath = "src/test/resources/ftl/colabaseDemo";
    final String mailAddress = "gux2025@gmail.com";
    final String edmCustomerName = "IISI";
    final String edmProject = "隨時清償";
    final String fromAddress = "citi@imta.citicorp.com";
    final String fromPerson = "花旗（台灣）銀行";
    final String edmHost = "smtp.gmail.com";
    final String edmUsr = "css123456tw@gmail.com";
    final String edmPwd = "kvzulwkqdoiprtfb";
    final String edmSubject = "花旗(台灣)銀行 圓滿貸線上申請確認通知函";
    final String edmAttachedFilePath = "src/test/resources/ftl/colabaseDemo/edmImages/kv.jpg";
    final String edmImageFileFolder = "src/test/resources/ftl/colabaseDemo/edmImages";

    @Before
    public void setUp() throws Exception {
        org.apache.log4j.BasicConfigurator.configure(new NullAppender());
    }
    
    @Test
    public void testDemo(){
        try {
            EDMService eDMService = Mockito.spy(new EDMServiceImpl());
            CapSystemConfig cc = Mockito.spy(new CapSystemConfig());
            Mockito.when(((CCBasePageReport) eDMService).getSysConfig()).thenReturn(cc);
            Mockito.when(cc.getProperty("mail.enable", "true")).thenReturn("true");
            
            FreeMarkerConfigurer fm = Mockito.spy(new FreeMarkerConfigurer());
            Mockito.when(((CCBasePageReport) eDMService).getFmConfg()).thenReturn(fm);
            Configuration cf = Mockito.spy(new Configuration());
            Mockito.when(fm.getConfiguration()).thenReturn(cf);
            
            TemplateLoader templateLoader=null;  
            templateLoader = new FileTemplateLoader(new File(edmFtlPath));
            cf.setTemplateLoader(templateLoader);
            Template t = Mockito.spy(cf.getTemplate(edmFtlName));
            Mockito.when(cf.getTemplate(edmFtlName)).thenReturn(t);
            
            EdmSetting edmSetting = new EdmSetting();
            edmSetting.setEdmFtlPath(edmFtlName);
            edmSetting.setMailAddress(mailAddress);
            edmSetting.setFromAddress(fromAddress);
            edmSetting.setFromPerson(fromPerson);
            edmSetting.setEdmHost(edmHost);
            edmSetting.setEdmUsr(edmUsr);
            edmSetting.setEdmPwd(edmPwd);
            edmSetting.setEdmSubject(edmSubject);
            edmSetting.setEdmAttachedFilePath(edmAttachedFilePath);
            edmSetting.setEdmImageFileFolder(edmImageFileFolder);
    
            Map<String, Object> ftlVar = new HashMap<String, Object>();
            ftlVar.put("otherAccountTitleMask", edmCustomerName);
            ftlVar.put("otherAccountNumberMask", edmProject);
            edmSetting.setMappingFtlVar(ftlVar);
            
            eDMService.sendEDM(edmSetting);
            
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

}