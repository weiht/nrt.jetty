package nrt.jetty.web;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.view.AbstractPathView;

public class ClasspathJspView
extends AbstractPathView
implements View {
	public ClasspathJspView(String dest) {
		super(dest);
	}

	public void render(HttpServletRequest req, HttpServletResponse resp,
			Object obj) throws Throwable {
        String path = evalPath(req, obj);
        String args = "";
        if (path != null && path.contains("?")) { //将参数部分分解出来
            args = path.substring(path.indexOf('?'));
            path = path.substring(0, path.indexOf('?'));
        }

        String ext = ".jsp";        
        // 空路径，采用默认规则
        if (Strings.isBlank(path)) {
            path = Mvcs.getRequestPath(req);
            path = "/WEB-INF"
                    + (path.startsWith("/") ? "" : "/")
                    + Files.renameSuffix(path, ext);
        }
        // 绝对路径 : 以 '/' 开头的路径不增加 '/WEB-INF'
        else if (path.charAt(0) == '/') {
            if (!path.toLowerCase().endsWith(ext))
                path += ext;
        }
        // 包名形式的路径
        else {
            path = "/WEB-INF/" + path.replace('.', '/') + ext;
        }

        // 执行 Forward
        path = path + args;
        RequestDispatcher rd = req.getRequestDispatcher(path);
        if (rd == null)
            throw Lang.makeThrow("Fail to find Forward '%s'", path);
        // Do rendering
        rd.forward(req, resp);
	}
}
