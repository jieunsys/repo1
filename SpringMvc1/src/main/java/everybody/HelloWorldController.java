package everybody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

@Controller
public class HelloWorldController extends AbstractController {

	@RequestMapping(value="/test1.do", method=RequestMethod.GET)
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
System.out.println("################ start");
		ModelAndView model = new ModelAndView("HelloWorldPage");
		model.addObject("msg", "helloworld 한글테스트에요1234");

		return model;
	}
}
