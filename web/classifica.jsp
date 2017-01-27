<!DOCTYPE html>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${lan.getLanSelected()}" />
<fmt:setBundle basename="Resources.string" />

<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="description" content="">
        <meta name="author" content="">

        <title>
            <fmt:message key="users.ranking"/>
            <c:if test="${utente != null}">
                - <c:out value="${utente.getNomeCognome()}"/>
            </c:if>
        </title>
        <c:set value="/base.jsp" scope="session" var="lastPage"/>

        <!-- Bootstrap Core CSS -->
        <link href="<%= request.getContextPath()%>/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

        <!-- Theme CSS -->
        <link href="<%= request.getContextPath()%>/css/freelancer.min.css" rel="stylesheet">

        <!-- Custom Fonts -->
        <link href="<%= request.getContextPath()%>/vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
        <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css">
        <link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet" type="text/css">

        <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
        <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
        <!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
            <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
        <![endif]-->
        <script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery-1.8.2.min.js"></script>
        <script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.mockjax.js"></script>
        <script type="text/javascript" src="<%= request.getContextPath()%>/src/jquery.autocomplete.js"></script>
        <script type="text/javascript" src="<%= request.getContextPath()%>/autocomplete.txt"></script>
        <script type="text/javascript" src="<%= request.getContextPath()%>/scripts/demo.js"></script>
    </head>

    <body id="page-top" class="index">

        <!-- Navigation -->
        <nav id="mainNav" class="navbar navbar-default navbar-fixed-top navbar-custom">
            <div class="container">
                <!-- Brand and toggle get grouped for better mobile display -->
                <div class="navbar-header page-scroll">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                        <span class="sr-only">Toggle navigation</span> Menu <i class="fa fa-bars"></i>
                    </button>
                    <a class="navbar-brand" href="<%= request.getContextPath()%>/HomeServlet">TRIPTNADVISOR</a>
                </div>

                <!-- Collect the nav links, forms, and other content for toggling -->
                <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">

                    <ul class="nav navbar-nav navbar-left">
                        <c:choose>
                            <c:when test="${utente == null}">
                                <li>
                                    <a href="<%= request.getContextPath()%>/registration.jsp"><fmt:message key="welcome.visitors"/></a>
                                </li>
                            </c:when>
                            <c:when test="${utente.isAmministratore()}">
                                <li>
                                    <button class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><img src="<%= request.getContextPath()%><c:out value="${utente.getAvpath()}"/>" HEIGHT="25" WIDTH="25" BORDER="0" align="center">  <c:out value="${utente.getNomeCognome()}"/>
                                        <span class="caret"></span></button>
                                    <ul class="dropdown-menu">
                                        <li><a href="<%= request.getContextPath()%>/private/ConfigurazioneProfilo"><fmt:message key="profile"/></a></li>
                                        <li><a href="<%= request.getContextPath()%>/private/LogoutServlet"><fmt:message key="exit"/></a></li>
                                    </ul>
                                </li>
                            </c:when>
                            <c:when test="${utente.isRegistrato()}">
                                <li>
                                    <button class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><img src="<%= request.getContextPath()%><c:out value="${utente.getAvpath()}"/>" HEIGHT="25" WIDTH="25" BORDER="0" align="center">  <c:out value="${utente.getNomeCognome()}"/>
                                        <span class="caret"></span></button>
                                    <ul class="dropdown-menu">
                                        <li><a href="<%= request.getContextPath()%>/private/ConfigurazioneProfilo"><fmt:message key="profile"/></a></li>
                                        <li><a href="<%= request.getContextPath()%>/private/ConfigurazioneAddRistorante"><fmt:message key="add.restaurant"/></a></li>
                                        <li><a href="<%= request.getContextPath()%>/private/LogoutServlet"><fmt:message key="exit"/></a></li>
                                    </ul>
                                </li>
                            </c:when>
                            <c:when test="${utente.isRistoratore()}">
                                <li>
                                    <button class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><img src="<%= request.getContextPath()%><c:out value="${utente.getAvpath()}"/>" HEIGHT="25" WIDTH="25" BORDER="0" align="center">  <c:out value="${utente.getNomeCognome()}"/>
                                        <span class="caret"></span></button>            
                                    <ul class="dropdown-menu">
                                        <li><a href="<%= request.getContextPath()%>/private/ConfigurazioneProfilo"><font class="dropdown-line"><fmt:message key="profile"/></font></a></li>
                                        <li><a href="<%= request.getContextPath()%>/privateRistoratore/ConfigurazioneRistoranti"><fmt:message key="my.restaurant"/></a></li>
                                        <li><a href="<%= request.getContextPath()%>/private/ConfigurazioneAddRistorante"><fmt:message key="add.restaurant"/></a></li>
                                        <li><a href="<%= request.getContextPath()%>/private/LogoutServlet"><fmt:message key="exit"/></a></li>
                                    </ul>
                                </li>
                            </c:when>
                        </c:choose>
                    </ul>
                    <ul class="nav navbar-nav navbar-right">
                        <c:choose>
                            <c:when test="${utente == null}">
                                <li>
                                    <a href="<%= request.getContextPath()%>/login.jsp"><fmt:message key="login"/></a>
                                </li>
                                <li>
                                    <a href="<%= request.getContextPath()%>/registration.jsp">
                                        <fmt:message key="register"/>
                                    </a>
                                </li>
                            </c:when>
                            <c:when test="${utente.isRistoratore() || utente.isAmministratore()}">
                                <li>
                                    <button class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><fmt:message key="notify"/>
                                        <span class="caret"></span></button>
                                    <ul class="dropdown-menu">
                                        <c:choose>
                                            <c:when test="${utente.getNotifiche().size()>0}">
                                                <c:forEach var="notifica" items="${utente.getNotifiche()}">
                                                    <li>
                                                        <a href="<%=request.getContextPath()%>/private/PrepareNotificheServlet?id_not=<c:out value="${notifica.getId()}"/>">
                                                            <c:out value="${notifica.toString()}"/>
                                                        </a>
                                                    </li>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                <li>
                                                    <a href="<%=request.getContextPath()%>/private/PrepareNotificheServlet?">
                                                        <fmt:message key="no.notify"/>
                                                    </a>
                                                </li>
                                            </c:otherwise>
                                        </c:choose>
                                    </ul>
                                </li>
                            </c:when>  
                        </c:choose>
                        <li>
                            <button class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
                                <img src="<%= request.getContextPath()%><fmt:message key="bandiera"/>" alt="- "/>
                                <fmt:message key="language"/>
                                <span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu">
                                <li><a href="<%= request.getContextPath()%>/ConfigLingua?l=en_GB"><img src="<%= request.getContextPath()%>/img/flags/GB.png" alt="- "/><fmt:message key="english"/></a></li>
                                <li><a href="<%= request.getContextPath()%>/ConfigLingua?l=it_IT"><img src="<%= request.getContextPath()%>/img/flags/IT.png" alt="- "/><fmt:message key="italian"/></a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
                <!-- /.navbar-collapse -->
            </div>
            <!-- /.container-fluid -->
        </nav>




        <!-- Header -->
        <header>
            <div class="container">
                <div class="row">
                    <div class="col-md-12">
                        <div class="intro-text">
                            <span class="name"><fmt:message key="users.ranking"/></span>
                            <hr class="star-light">
                        </div>
                    </div>
                </div>
            </div>
        </header>


        <!-- Portfolio Grid Section -->
        <section id="portfolio">
            <div class="container">
                <c:forEach var="utente" items="${classifica}">
                    <div class="row">
                        <div class="col-md-6">
                            <img src="<%= request.getContextPath()%><c:out value="${utente.getAvpath()}"/>" alt="<fmt:message key="no.img"/>" height="200">
                        </div>
                        <div class="col-md-6">
                            <label class="control-label"><fmt:message key="position.ranking"/>: <c:out value="${utente.getPosizioneClassifica()}"/></label>
                            <br>
                            <label class="control-label"><c:out value="${utente.getNome()}"/></label>
                            <br>
                            <label class="control-label"><c:out value="${utente.getCognome()}"/></label>
                            <br>
                            <label class="control-label"><fmt:message key="rank"/>: <c:out value="${utente.getReputazione()}"/></label>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <c:if test="${utente != null}">
                <form action="<%= request.getContextPath()%>/ConfigurazioneProfilo" method="get">
                    <button class="btn btn-default" type="submit">
                        <fmt:message key="go.profile"/>
                    </button>
                </form>
            </c:if>
            <br>
            <form action="<%= request.getContextPath()%>/HomeServlet" method="get">
                <button class="btn btn-default" type="submit">
                    <fmt:message key="go.home"/>
                </button>
            </form>



        </section>


        <!-- Footer -->
        <footer class="text-center">
            <div class="footer-above">
                <div class="container">
                    <div class="row">
                        <div class="footer-col col-md-6">
                            <h3>Location</h3>
                            <p>Polo Ferrari, Via Sommarive 5
                                <br>TRENTO, TN 38100</p>
                        </div>
                        <div class="footer-col col-md-6">
                            <h3>About TripTNadvisor</h3>
                            <p>TripTNadvisor is free to use, developed by UNITN students on 2016</p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="footer-below">
                <div class="container">
                    <div class="row">
                        <div class="col-lg-12">
                            Copyright &copy; TRIPTNADVISOR 2016
                        </div>
                    </div>
                </div>
            </div>
        </footer>


        <!-- Scroll to Top Button (Only visible on small and extra-small screen sizes) -->

        <div class="scroll-top page-scroll hidden-sm hidden-xs hidden-lg hidden-md">
            <a class="btn btn-primary" href="#page-top">
                <i class="fa fa-chevron-up"></i>
            </a>
        </div>



        <!-- jQuery -->
        <script src="<%= request.getContextPath()%>/vendor/jquery/jquery.min.js"></script>

        <!-- Bootstrap Core JavaScript -->
        <script src="<%= request.getContextPath()%>/vendor/bootstrap/js/bootstrap.min.js"></script>

        <!-- Plugin JavaScript -->
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-easing/1.3/jquery.easing.min.js"></script>

        <!-- Theme JavaScript -->
        <script src="<%= request.getContextPath()%>/js/freelancer.min.js"></script>

    </body>

</html>
