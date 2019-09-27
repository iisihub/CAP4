<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE>
<html>
<head>
<meta name="decorator" content="none">
<title><spring:message code="sequence.title">
        <!--語系-->
    </spring:message></title>
</head>
<body>
    <script>
          loadScript('js/system/sequence');
        </script>
    <div>
        <div id="sequenceDialog" class="hide" title="<spring:message code="js.sequence.getNewSeq" />">
            <form id="sform" onsubmit="return false;">
                <table class="row-data">
                    <tr>
                        <th><spring:message code="js.sequence.seqNode">
                                <!--流水號代碼-->
                            </spring:message></th>
                        <td><input type="text" id="seqNode" name="seqNode" maxlength="20" class="validate[required]" />
                            <button id="getNewSeq" type="button" class="btn1">
                                <spring:message code="js.sequence.getNewSeq">
                                    <!--取得序號 -->
                                </spring:message>
                            </button>
                            <span id="theSeq" class="field"></span></td>
                    </tr>
                </table>
            </form>
        </div>
        <div class="btns">
            <button id="qry" type="button" class="btn1">
                <spring:message code="btn.query">
                    <!--查詢-->
                </spring:message>
            </button>
            <button id="sequence" type="button" class="btn1">
                <spring:message code="js.sequence.getNewSeq">
                    <!--取得序號 -->
                </spring:message>
            </button>
        </div>
        <div id="qryDialog" class="hide" title="<spring:message code="btn.query" />">
            <form id="qform" onsubmit="return false;">
                <table class="row-data">
                    <tr>
                        <th><spring:message code="js.sequence.seqNode">
                                <!--流水號代碼-->
                            </spring:message></th>
                        <td><input type="text" id="seqNode" name="seqNode" maxlength="20" class="validate[required]" /></td>
                    </tr>
                </table>
            </form>
        </div>
        <div id="gridview"></div>
    </div>
</body>
</html>
