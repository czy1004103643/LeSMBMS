<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="fm" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Title</title>
    <style>
        #user span[id]{
            color: red;
        }
    </style>
</head>
<body>
    <fm:form modelAttribute="user" method="post">
        <fm:errors path="userCode"/><br>
        用户编码：<fm:input path="userCode"/><br>
        <fm:errors path="userName"/><br>
        用户名称：<fm:input path="userName"/><br>
        <fm:errors path="userPassword"/><br>
        用户密码：<fm:password path="userPassword"/><br>
        <fm:errors path="birthday"/><br>
        用户生日：<fm:input path="birthday" class="Wdate"
                       onclick="WdatePicker();" readonly="true"/><br>
        用户地址：<fm:input path="address"/><br>
        <fm:errors path="phone"/><br>
        联系电话：<fm:input path="phone"/><br>
        用户角色：
        <fm:select path="userRole">
            <fm:option value="1">系统管理员</fm:option>
            <fm:option value="2">经理</fm:option>
            <fm:option value="3">普通用户</fm:option>
        </fm:select><br>
        <input type="submit" value="保存">
    </fm:form>
    <script type="text/javascript" src="${pageContext.request.contextPath}/statics/js/jquery-1.8.3.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/statics/calendar/config.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/statics/calendar/WdatePicker.js"></script>
</body>
</html>
