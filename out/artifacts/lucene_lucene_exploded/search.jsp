<%@ page language="java" contentType="text/html; charset=UTF-8" isELIgnored="false" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>千度一下，你就知道</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
</head>

<body>
<form action="search.do" method="post">
    <input name="num" value="1" type="hidden"/>
    <input name="keywords" maxlength="30"/>
    <input type="submit" value="千度一下"/>
</form>
<br/>
<c:if test="${! empty page.list}" >
千度为您找到相关结果约${page.rowCount }个
    <br/>
<c:forEach items="${page.list}" var="hb">
    <a href="${hb.url}">${hb.title}</a>
    <p>${hb.context}</p>
    ${hb.url}
    <br/>
    <br/>
</c:forEach>
    <br/>
    <c:if test="${page.hasPrevious}" >
        <a href="search.do?num=${page.previousPageNum}&keywords=${keywords}" >上一页</a>
    </c:if>
    <c:forEach begin="${page.everyPageStart}" end="${page.everyPageEnd}" var="current">
        <c:choose >
            <c:when test="${current eq page.pageNum}">
                <a ><font color="#a52a2a">${current}</font></a>
            </c:when>
            <c:otherwise>
                <a href="search.do?num=${current}&keywords=${keywords}">${current}</a>
            </c:otherwise>
        </c:choose>
        &nbsp;&nbsp;&nbsp;
    </c:forEach>
    <c:if test="${page.hasNext}" >
        <a href="search.do?num=${page.nextPageNum}&keywords=${keywords}">下一页</a>
    </c:if>
</c:if>
</body>
</html>