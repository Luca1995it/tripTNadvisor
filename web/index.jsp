
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
            <fmt:message key="home"/>
            <c:if test="${utente != null}">
                - <c:out value="${utente.getNomeCognome()}"/>
            </c:if>
        </title>
        <c:set value="/index.jsp" scope="session" var="lastPage"/>

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
        <script type="text/javascript" src="<%= request.getContextPath()%>/scripts/demo.js"></script>
        <script type="text/javascript" src="<%= request.getContextPath()%>/customScript/utility.js"></script>
        <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
        <link rel="stylesheet" href="/resources/demos/style.css">
        <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
        <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>

    </head>

    <body id="page-top" class="index" onload="setupAutocomplete('<%= request.getContextPath()%>/autocompleteRist.txt', '<%= request.getContextPath()%>/autocompletePlace.txt')">

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
                    <ul class="nav navbar-nav">
                        <li>
                            <label class="label label-success"><c:out value="${message}"/></label>
                        </li>
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
                                                            <c:out value="${notifica.toStringReduced()}"/>
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
                    <img class="img-responsive" src="<%= request.getContextPath()%>/img/profile.jpeg" alt="<fmt:message key="no.img"/>" height="200" width="400">
                    <div class="intro-text">
                        <span class="name"><fmt:message key="index.spot"/></span>
                    </div>
                    <hr class="star-light">
                    <div class="col-md-2"></div>
                    <div class="col-md-8">
                        <form role="search" method="POST" action="<%= request.getContextPath()%>/SearchServlet">
                            <div class="row">
                                <div class="form-group">
                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <div class="col-md-8">
                                            <div class="row">
                                                <label class="control-label"><fmt:message key="search"/></label>
                                                <input size="50" type="text" placeholder="<fmt:message key="cerca"/>" class="form-control" name="research" id="autocomplete_jquery1"/> 
                                                <br>
                                                <label class="control-label" id="luogoLabel"><fmt:message key="near"/></label>
                                                <input size="50" type="text" placeholder="<fmt:message key="place"/>" class="form-control" name="place" id="autocomplete_jquery2"/>
                                                <br>
                                                <div class="row">
                                                    <div class="col-md-6">
                                                        <button type="submit" class="btn btn-primary"><fmt:message key="search"/></button>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <div id="divSample" class="hideClass">
                                                            <input hidden type="text" id="Latitude" name="Latitude"/>
                                                            <input hidden type="text" id="Longitude" name="Longitude"/>
                                                        </div>
                                                        <div id="temp" hidden><h5><fmt:message key="receiving"/></h5></div>
                                                        <div id="received" hidden><h5><fmt:message key="received"/></h5></div>
                                                        <button class="btn btn-primary" id="getLoc" type="button" onclick="getLocationConstant()"><fmt:message key="getLoc"/></button>
                                                    </div>
                                                </div>
                                            </div>
                                            <br>
                                            <h5 class=""><fmt:message key="refine"/></h5>
                                            <div class="radio">
                                                <h5>
                                                    <label><input type="radio" name="tipo" value="all" checked="checked"/><fmt:message key="all"/> | </label>
                                                    <label><input type="radio" name="tipo" value="must"/><fmt:message key="most.visited"/></label>
                                                </h5>
                                            </div>
                                            <br>
                                            <h5 class=""><fmt:message key="filter.by.spec"/></h5>
                                            <div class="radio">
                                                <h5>
                                                    <label>
                                                        <input type="radio" name="spec" value="all" checked="checked"/><fmt:message key="all"/></label>
                                                        <c:forEach var="speci" items="${cucine}">
                                                        <label class="control-label"><input type="radio" name="spec" value="<c:out value="${speci}"/>"/><fmt:message key='${speci}'/></label>
                                                        </c:forEach>
                                                </h5>
                                            </div>
                                        </div>
                                        <div class="col-md-2"></div>
                                    </div>
                                </div>

                            </div>
                        </form>



                    </div>
                    <div class="col-md-2"></div>
                </div>
            </div>
        </header>


        <!-- Ristoranti più votati -->
        <section>
            <div class="container">
                <div class="row">
                    <div class="col-md-12 text-center">
                        <h2><fmt:message key="most.voted"/></h2>
                        <hr class="star-primary">
                    </div>
                </div>

                <div class="row">
                    <c:forEach var="ristorante" items="${mostVoted}">
                        <div class="col-md-4">
                            <h5>
                                <a href="<%= request.getContextPath()%>/ConfigurazioneRistorante?id_rist=<c:out value="${ristorante.getId()}"/>" class="portfolio-link" data-toggle="modal">
                                    <c:out value="${ristorante.getNome()}"/><br>
                                    <fmt:message key="users.vote"/>
                                    <c:if test="${ristorante.getVoto() == 0.0}">
                                        <fmt:message key="no.vote"/>
                                    </c:if>
                                    <c:if test="${ristorante.getVoto() > 0}">
                                        <c:out value="${ristorante.getVoto()}"/>
                                    </c:if><br><br>
                                    <c:if test="${ristorante.getFoto().size() > 0}">
                                        <img src="<%= request.getContextPath()%><c:out value="${ristorante.getFoto().get(0).getFotopath()}"/>" class="img-responsive" width="200" alt="<fmt:message key="no.img"/>" height="200">
                                    </c:if>
                                </a>
                            </h5>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </section>


        <!-- Ristoranti più visti -->
        <section>
            <div class="container">
                <div class="row">
                    <div class="col-md-12 text-center">
                        <h2><fmt:message key="most.seen"/></h2>
                        <hr class="star-primary">
                    </div>
                </div>

                <div class="row">
                    <c:forEach var="ristorante" items="${mostSeen}">
                        <div class="col-md-4">
                            <h5>
                                <a href="<%= request.getContextPath()%>/ConfigurazioneRistorante?id_rist=<c:out value="${ristorante.getId()}"/>" class="portfolio-link" data-toggle="modal">
                                    <c:out value="${ristorante.getNome()}"/><br>
                                    <fmt:message key="visits"/>
                                    <c:out value="${ristorante.getVisite()}"/><br><br>
                                    <c:if test="${ristorante.getFoto().size() > 0}">
                                        <img src="<%= request.getContextPath()%><c:out value="${ristorante.getFoto().get(0).getFotopath()}"/>" class="img-responsive" alt="<fmt:message key="no.img"/>" width="200">
                                    </c:if>
                                </a>
                            </h5>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </section>


        <!-- Recensioni più utili -->
        <section>
            <div class="container">
                <div class="row">
                    <div class="col-md-12 text-center">
                        <h2><fmt:message key="last.rec"/></h2>
                        <hr class="star-primary">
                    </div>
                </div>

                <div class="row">
                    <c:forEach var="rec" items="${lastRec}">
                        <div class="col-md-4">
                            <h5>

                                <a href="<%= request.getContextPath()%>/ConfigurazioneRistorante?id_rist=<c:out value="${rec.getRistorante().getId()}"/>" class="portfolio-link" data-toggle="modal">
                                    <fmt:message key="by"/> 
                                    <c:out value="${rec.getUtente().getNomeCognome()}"/>
                                    (<c:out value="${rec.getUtente().getReputazione()}"/>), 
                                    <c:out value="${rec.getData()}"/><br>
                                    <fmt:message key="val.media"/>: <c:out value="${rec.getMediaVoti()}"/>
                                </a>

                                <br><br>
                                <img src="<%= request.getContextPath()%><c:out value="${rec.getFotoPath()}"/>" class="img-responsive" width="200" alt="<fmt:message key="no.img"/>">
                                <br>
                                <label class="control-label">

                                    <c:out value="${rec.getTesto()}"/>

                                </label>
                                <br>
                                <label class="control-label">
                                    <c:if test="${rec.getCommento()!=null}">
                                        <fmt:message key="answer"/><br><c:out value="${rec.getCommento()}"/>
                                    </c:if>
                                </label>
                            </h5>

                        </div>
                    </c:forEach>
                </div>
            </div>
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


        <!-- Geolocation -->
        <script src="<%= request.getContextPath()%>/js/geoloc.js" type="text/javascript"></script>

        <!-- Bootstrap Core JavaScript -->
        <script src="<%= request.getContextPath()%>/vendor/bootstrap/js/bootstrap.min.js"></script>

        <!-- Plugin JavaScript -->
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-easing/1.3/jquery.easing.min.js"></script>

        <!-- Theme JavaScript -->
        <script src="<%= request.getContextPath()%>/js/freelancer.min.js"></script>

    </body>

</html>
