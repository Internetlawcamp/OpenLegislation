<%@ page language="java" import="java.util.*,java.io.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.committee.*,javax.xml.bind.*" contentType="text/xml" pageEncoding="utf-8"%><%

 
 Calendar calendar = (Calendar)request.getAttribute("calendar");


 %><%=JsonConverter.getJson(calendar).toString()%>