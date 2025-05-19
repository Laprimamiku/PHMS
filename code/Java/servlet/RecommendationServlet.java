package servlet;

import service.RecommendationService;
import model.Pet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/recommendation")
public class RecommendationServlet extends HttpServlet {
    private RecommendationService recommendationService;

    @Override
    public void init() throws ServletException {
        recommendationService = new RecommendationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        // 未登录则跳转登录页面
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 更新相似度数据（建议实际部署时移至定时任务中）
        recommendationService.updatePetSimilarities();
        recommendationService.updateUserSimilarities();

        // 获取推荐列表
        List<Pet> itemBased = recommendationService.getItemBasedRecommendations(userId);
        List<Pet> userBased = recommendationService.getUserBasedRecommendations(userId);

        // 设置请求属性并转发到展示页面
        request.setAttribute("itemBasedRecommendations", itemBased);
        request.setAttribute("userBasedRecommendations", userBased);
        request.getRequestDispatcher("favorite_recommend.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // POST 请求与 GET 请求相同处理逻辑
        doGet(request, response);
    }
}
