package com.zben.test.springmvc.servlet;

import com.zben.test.springmvc.annotation.*;
import com.zben.test.springmvc.controller.UserController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zben
 * @Description:在springmvc中，DispatchServlet是核心，说到底还是HttpServlet得子类，
 * 因此我这边自己的DispatcherServlet需要extends HttpServlet
 * @Date: 下午2:49 2018/3/27
 */
@WebServlet(name = "disparcherServlet", urlPatterns = "/*", loadOnStartup = 1,
            initParams = {@WebInitParam(name = "back-package", value = "com.zben.test.springmvc")})
public class DispatcherServlet extends HttpServlet {

    //扫描的基包
    private String basePackage = "";

    //基包下面所有的带包路径全限定类名
    private List<String> packageNames = new ArrayList<String>();

    //注解实例化 注解上的名称：实例化对象
    private Map<String, Object> instanceMap = new HashMap<String, Object>();

    //带包路径的全限定类名： 注解上的名称
    private Map<String, String> nameMap = new HashMap<String, String>();

    //URL地址和方法的映射关系 springMvc就是方法调用链
    private Map<String, Method> urlMethodMap = new HashMap<String, Method>();

    //Method和全限定类名映射关系  主要是为了通过Method找到该方法的对象利用反射执行
    private Map<Method, String> methodPackageMap = new HashMap<Method, String>();


    @Override
    public void init(ServletConfig config) throws ServletException {
        basePackage = config.getInitParameter("back-package");

        try {
            //1. 扫描基包得到全部的带包路径权限定类名
            scanBasePackage(basePackage);
            //2. 把带有@Controller/@Service/@Repository的类实例化放入Map中，KEY为注解上的名称
            instance(packageNames);
            //3. spring ioc注入
            springIOC();
            //4. 完成URL地址和方法的映射关系
            handlerUrlMethodMap();
            System.out.println("1");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void scanBasePackage(String basePackage) {
        //注意为了得到基包下面的url路径需要对basePackage做转换：将.替换为/
        //  User/zhouben/Download/apache-tomcat-7.0.78/webapps/springmvc_1/WEB-INF/classes/com/zben/test/springmvc
        URL url = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
        File basePackageFile = new File(url.getPath());
        System.out.println("scan: " + basePackageFile);

        File[] childFiles = basePackageFile.listFiles();
        for (File file : childFiles) {
            if (file.isDirectory()) {   //目录继续递归扫描
                scanBasePackage(basePackage + "." + file.getName());
            } else if (file.isFile()) {
                //类似这种：com.zben.test.spring.service.impl.UserServiceImpl.class  去掉class
                packageNames.add(basePackage + "." + file.getName().split("\\.")[0]);
            }
        }
    }

    /**
     * 从这里你可以看出，我们完成了被注解标注的类的实例化，以及和注解名称的映射。
     * @param packageNames
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void instance(List<String> packageNames) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (packageNames.size() < 1) {
            return;
        }

        for (String string : packageNames) {
            Class clazz = Class.forName(string);
            if (clazz.isAnnotationPresent(Controller.class)) {
                Controller controller = (Controller) clazz.getAnnotation(Controller.class);
                String controllerName = controller.value();

                instanceMap.put(controllerName, clazz.newInstance());
                nameMap.put(string, controllerName);
                System.out.println("Controller : " + string + " , value : " + controller.value());
            } else if (clazz.isAnnotationPresent(Service.class)) {
                Service service = (Service) clazz.getAnnotation(Service.class);
                String serviceName = service.value();

                instanceMap.put(serviceName, clazz.newInstance());
                nameMap.put(string, serviceName);
                System.out.println("Service : " + string + " , value : " + service.value());
            } else if (clazz.isAnnotationPresent(Repository.class)) {
                Repository repository = (Repository) clazz.getAnnotation(Repository.class);
                String repositoryName = repository.value();

                instanceMap.put(repositoryName, clazz.newInstance());
                nameMap.put(string, repositoryName);
                System.out.println("Repository : " + string + " , value : " + repository.value());
            }
        }
    }

    private void springIOC() throws IllegalAccessException {
        for (Map.Entry<String, Object> entry : instanceMap.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Qualifier.class)) {
                    String name = field.getAnnotation(Qualifier.class).value();
                    field.setAccessible(true);
                    field.set(entry.getValue(), instanceMap.get(name));
                }
            }
        }
    }

    private void handlerUrlMethodMap() throws ClassNotFoundException {
        if (packageNames.size() < 1) {
            return;
        }

        for (String string : packageNames) {
            Class clazz = Class.forName(string);
            if (clazz.isAnnotationPresent(Controller.class)) {
                Method[] methods = clazz.getMethods();
                StringBuffer baseUrl = new StringBuffer();
                if (clazz.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
                    baseUrl.append(requestMapping.value());
                }

                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        baseUrl.append(requestMapping.value());
                        urlMethodMap.put(baseUrl.toString(), method);
                        methodPackageMap.put(method, string);
                    }
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.replaceAll(contextPath, "");

        //通过path找到Method
        Method method = (Method) urlMethodMap.get(path);
        if (method != null) {
            //通过Method拿到Controller对象，准备反射执行
            String packageName = methodPackageMap.get(method);
            String controllerName = nameMap.get(packageName);

            //拿到Controller对象
            UserController userController = (UserController) instanceMap.get(controllerName);
            try {
                method.setAccessible(true);
                method.invoke(userController);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
    }
}



































