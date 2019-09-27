<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE>
<html>
<head>
<meta name="decorator" content="none">
<title><spring:message code="codetype.title">
        <!--共用參數-->
    </spring:message></title>
</head>
<body>
    <script>
          loadScript('js/system/codetype');
        </script>
    <div>
        <div class="btns">
            <button id="qry" type="button" class="btn1">
                <spring:message code="btn.query">
                    <!--查詢-->
                </spring:message>
            </button>
            <button id="add" type="button" class="btn1">
                <spring:message code="btn.add">
                    <!--新增-->
                </spring:message>
            </button>
            <button id="modify" type="button" class="btn1">
                <spring:message code="btn.modify">
                    <!--修改-->
                </spring:message>
            </button>
            <button id="delete" type="button" class="btn1">
                <spring:message code="btn.delete">
                    <!--刪除-->
                </spring:message>
            </button>
        </div>
        <div id="qryDialog" class="hide" title="<spring:message code="btn.query" />">
            <form id="qform" onsubmit="return false;">
                <table class="row-data">
                    <tr>
                        <th><spring:message code="codetype.local">
                                <!--語系-->
                            </spring:message></th>
                        <td><select id="locale" name="locale" combokey="lang" class="validate[required]"></select></td>
                    </tr>
                    <tr>
                        <th><spring:message code="codetype.codeType">
                                <!--代碼-->
                            </spring:message></th>
                        <td><input type="text" id="codeType" name="codeType" maxlength="32" class="validate[required,funcCall[maxUTF8[32]]]" /></td>
                    </tr>
                </table>
            </form>
        </div>
        <div id="editDialog" class="hide" title="<spring:message code="js.edit" />">
            <form id="eform" onsubmit="return false;">
                <input type="text" id="oid" name="oid" class="hide" />
                <table class="row-data">
                    <tr>
                        <th><spring:message code="codetype.local">
                                <!--語系-->
                            </spring:message></th>
                        <td><select id="locale" name="locale" combokey="lang" class="validate[required]"></select></td>
                    </tr>
                    <tr>
                        <th><spring:message code="codetype.codeType">
                                <!--代碼-->
                            </spring:message></th>
                        <td><input type="text" id="codeType" name="codeType" maxlength="32" class="validate[required,funcCall[maxUTF8[32]]]" /></td>
                    </tr>
                    <tr>
                        <th><spring:message code="codetype.codeVal">
                                <!--值-->
                            </spring:message></th>
                        <td><input type="text" id="codeValue" maxlength="48" name="codeValue" class="validate[required,funcCall[maxUTF8[48]]]" /></td>
                    </tr>
                    <tr>
                        <th><spring:message code="codetype.codeDesc">
                                <!--描述-->
                            </spring:message></th>
                        <td><input type="text" id="codeDesc" name="codeDesc" maxlength="100" class="validate[funcCall[maxUTF8[100]]]" /></td>
                    </tr>
                    <tr>
                        <th><spring:message code="codetype.codeOrder">
                                <!--排序-->
                            </spring:message></th>
                        <td><input type="text" id="codeOrder" name="codeOrder" maxlength="3" size="3" class="validate[required,custom[integer],funcCall[maxUTF8[3]]] numeric" /></td>
                    </tr>
                </table>
            </form>
        </div>
        <div id="gridview"></div>
    </div>
</body>
</html>
