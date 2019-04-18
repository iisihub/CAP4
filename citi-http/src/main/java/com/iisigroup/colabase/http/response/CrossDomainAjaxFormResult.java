package com.iisigroup.colabase.http.response;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.component.impl.StringResponse;
import com.iisigroup.cap.utils.CapString;

/**<pre>
 * Cross Domain Ajax Form Result
 * </pre>
 * @since  2015年4月
 * @author YungLin
 * @version <ul>
 *          <li>2015年4月,YungLin,new
 *          <li>2016年1月22日,Tim,VAforM3:Consider generating a POST request instead of a GET request.
 *          </ul>
 */
public class CrossDomainAjaxFormResult extends AjaxFormResult {
    private static final long serialVersionUID = -5837154707383083567L;
    private String callback;
    private String corsDomain;

    /* (non-Javadoc)
     * @see com.iisigroup.cap.component.impl.AjaxFormResult#respondResult(javax.servlet.ServletResponse)
     */
    @Override
    public void respondResult(ServletResponse response) {
    	HttpServletResponse resp = (HttpServletResponse)response;
    	resp.addHeader("Access-Control-Allow-Origin", corsDomain);
        if(!CapString.isEmpty(callback) && !"AjaxResultData".equals(callback)){
        	new StringResponse(getContextType(), getEncoding(), callback + "(" + getResult() + ")").respond(response);
        }else{
        	resp.addHeader("Access-Control-Allow-Methods", "POST");
        	super.respondResult(response);
        }
    }// ;

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

	public String getCorsDomain() {
		return corsDomain;
	}

	public void setCorsDomain(String corsDomain) {
		this.corsDomain = corsDomain;
	}

}
