package servlet;

import model.Pet;
import service.PetService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@WebServlet("/PetServlet")
@MultipartConfig(location = "C:\\upload", maxFileSize = 1024 * 1024 * 10) // 设置上传文件的存储路径和最大文件大小
public class PetServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private PetService petService = new PetService();

    // 创建一个通用的方法来设置 Pet 对象的属性
    private Pet createPetFromRequest(HttpServletRequest request) {
        String name = request.getParameter("name");
        String type = request.getParameter("type");
        String breed = request.getParameter("breed"); 
        int age = Integer.parseInt(request.getParameter("age"));
        String description = request.getParameter("description");
        String status = request.getParameter("status");

        Pet pet = new Pet();
        pet.setName(name);
        pet.setType(type);
        pet.setBreed(breed);
        pet.setAge(age);
        pet.setDescription(description);
        pet.setStatus(status != null ? status : "available");  // 默认状态为 "available"
        pet.setAdopted(status != null ? status : "no");
        
        // 获取当前登录用户的ID并关联
        Integer userId = (Integer) request.getSession().getAttribute("userId");  // 假设登录用户的ID保存在 session 中
        if (userId != null) {
            pet.setUser_id(userId);  // 设置宠物所属用户
        }

        // 如果是更新操作，设置宠物ID
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            pet.setId(Integer.parseInt(idStr));
        }

        return pet;
    }

    // 处理POST请求
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            // 登录状态检查
            if (request.getSession().getAttribute("userId") == null) {
                response.sendRedirect("log.jsp");  // 未登录，跳转到登录页面
                return;
            }

            String uploadPath = getServletContext().getRealPath("") + File.separator + "upload";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();  // 如果文件夹不存在，创建文件夹
            }

            // 获取多个文件
            Collection<Part> parts = request.getParts();
            List<String> imageUrls = new ArrayList<>();  // 用于保存图片URL

            for (Part part : parts) {
                if ("images".equals(part.getName()) && part.getSize() > 0) {
                    String fileName = part.getSubmittedFileName();
                    if (fileName != null && !fileName.isEmpty()) {
                        String filePath = uploadPath + File.separator + fileName;
                        part.write(filePath);  // 保存文件到磁盘
                        imageUrls.add("upload/" + fileName);  // 添加图片的相对路径
                    }
                }
            }
            
            // 创建宠物对象并设置其他属性
            Pet pet = createPetFromRequest(request);
            
            // 如果有上传图片，设置图片URL
            if (!imageUrls.isEmpty()) {
                pet.setImage_url(String.join(",", imageUrls));  // 将多个图片路径用逗号连接
            }

            // 调用Service层方法来保存宠物信息
            Integer userId = (Integer) request.getSession().getAttribute("userId");
            boolean success = petService.addPet(userId, pet);

            if (success) {
                request.getSession().setAttribute("message", "宠物信息上传成功！");
                response.sendRedirect("PetServlet?action=adopthome");  // 上传成功后跳转到宠物列表
                return;
            } else {
                request.setAttribute("errorMessage", "上传宠物信息失败，请稍后再试！");
                request.getRequestDispatcher("PetServlet?action=adopthome").forward(request, response);  
                return;
            }
        }
        
     // 更新宠物信息
        else if ("update".equals(action)) {
            try {
                String uploadPath = getServletContext().getRealPath("") + File.separator + "upload";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();  // 如果文件夹不存在，创建文件夹
                }

                // 获取多个文件
                Collection<Part> parts = request.getParts();
                List<String> imageUrls = new ArrayList<>();  // 用于保存新上传图片的URL

                for (Part part : parts) {
                    if ("images".equals(part.getName()) && part.getSize() > 0) {
                        String fileName = part.getSubmittedFileName();
                        if (fileName != null && !fileName.isEmpty()) {
                            String filePath = uploadPath + File.separator + fileName;
                            part.write(filePath);  // 保存文件到磁盘
                            imageUrls.add("upload/" + fileName);  // 添加图片的相对路径
                        }
                    }
                }

                // 创建宠物对象并设置其他属性
                Pet pet = createPetFromRequest(request);

                // 获取领养状态并设置
                String adopted = request.getParameter("adopted");
                pet.setAdopted(adopted);

                // 如果有上传图片，设置新的图片URL
                if (!imageUrls.isEmpty()) {
                    pet.setImage_url(String.join(",", imageUrls));  // 将多个图片路径用逗号连接
                } else {
                    // 如果没有上传新图片，则保留原来的图片
                    Pet existingPet = petService.getPetById(pet.getId());
                    if (existingPet != null) {
                        pet.setImage_url(existingPet.getImage_url());  // 使用原有图片路径
                    }
                }

                // 调用Service层方法来更新宠物信息
                boolean success = petService.updatePet(pet);

                if (success) {
                    response.sendRedirect("pet_manage.jsp");  // 更新成功后跳转到宠物列表
                    return;
                } else {
                    request.setAttribute("errorMessage", "更新宠物信息失败，请稍后再试！");
                    request.getRequestDispatcher("pet_edit.jsp?id=" + pet.getId()).forward(request, response); 
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "更新宠物信息时发生错误，请稍后再试！");
                String petId = request.getParameter("id");
                request.getRequestDispatcher("pet_edit.jsp?id=" + petId).forward(request, response);  // 使用forward而非sendRedirect
            }
        }
        
        // 领养操作
        else if ("adopt".equals(action)) {
            Integer userId = (Integer) request.getSession().getAttribute("userId");
            if (userId == null) {
                response.sendRedirect("log.jsp");  // 确保没有调用其他响应操作
                return;
            }
            int petId = Integer.parseInt(request.getParameter("petId"));
            try {
                // 调用Service方法，更新宠物的adopted状态并设置adopt_id
                petService.updateAdoptedStatusWithAdoptId(petId, "undetermined", userId);
                // 设置操作完成消息，确保不会提前输出响应
                request.getSession().setAttribute("message", "宠物已标记为待领养！");
                // 重定向回宠物列表页面
                response.sendRedirect("PetServlet?action=adopthome");
                return;
            } catch (IllegalArgumentException e) {
                // 错误信息处理
                request.setAttribute("errorMessage", e.getMessage());
                response.sendRedirect("PetServlet?action=adopthome");
                return;
            }
        }

        // 领养操作
        else if ("approveAdopt".equals(action)) {
            int petId = Integer.parseInt(request.getParameter("id"));
            Pet pet = petService.getPetById(petId);
            if (pet != null && "undetermined".equals(pet.getAdopted())) {
                pet.setAdopted("yes");
                petService.updatePet(pet);  // 更新领养状态为 yes
            }
            response.sendRedirect("PetServlet?action=adoptList");
            return;
        }
        
        //拒绝领养
        else if ("rejectAdopt".equals(action)) {
            int petId = Integer.parseInt(request.getParameter("id"));
            Pet pet = petService.getPetById(petId);
            if (pet != null && "undetermined".equals(pet.getAdopted())) {
                pet.setAdopted("no");
                petService.updatePet(pet);  // 更新领养状态为 no
            }
            response.sendRedirect("PetServlet?action=adoptList");
            return;
        }
        
     
        
     // 审批宠物信息
        else if ("approve".equals(action)) {
            int petId = Integer.parseInt(request.getParameter("id"));
            petService.approvePet(petId);
            // 设置操作完成消息
            request.getSession().setAttribute("message", "宠物已通过审核");
        } else if ("reject".equals(action)) {
            int petId = Integer.parseInt(request.getParameter("id"));
            petService.rejectPet(petId);
            // 设置操作完成消息
            request.getSession().setAttribute("message", "宠物已拒绝审核");
        }
        // 重定向回审核页面
        response.sendRedirect("PetServlet?action=approveList");
        return;
    }
      
    // 处理GET请求
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {  		
        String action = request.getParameter("action");

        // 获取adopted = 'no' AND status = 'approved'的宠物并进行分页
        if ("adopthome".equals(action)) {
            // 获取当前页码，默认值为 1
            int currentPage = 1;
            String pageStr = request.getParameter("page");
            if (pageStr != null) {
                try {
                    currentPage = Integer.parseInt(pageStr);
                    // 确保页数最小为 1
                    if (currentPage < 1) {
                        currentPage = 1;
                    }
                } catch (NumberFormatException e) {
                    // 如果参数不是数字，可以选择不做任何处理，使用默认的 1
                }
            }

            // 每页显示 10 条
            int pageSize = 10;
            // 计算偏移量
            int offset = (currentPage - 1) * pageSize;

            // 获取符合条件的宠物并进行分页
            List<Pet> approvedPets = petService.getApprovedPetsForAdoption(offset, pageSize);

            // 获取总页数
            int totalApprovedPets = petService.getTotalApprovedPetsCount();
            int totalPages = (int) Math.ceil(totalApprovedPets / (double) pageSize);

            // 将数据设置到请求中
            request.setAttribute("approvedPets", approvedPets);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);

            // 转发到 JSP 页面显示结果
            request.getRequestDispatcher("adopt_home.jsp").forward(request, response);
        }
    


        // 删除宠物信息
        else if ("delete".equals(action)) {
            int petId = Integer.parseInt(request.getParameter("id"));
            boolean success = petService.deletePet(petId); // 调用 Service 层删除宠物
            if (success) {
                request.getSession().setAttribute("message", "宠物信息删除成功！");
            } else {
                request.setAttribute("errorMessage", "删除宠物信息失败，请稍后再试！");
            }
            response.sendRedirect("pet_manage.jsp");
        }

        // 查看单个宠物信息（详细信息）
        else if ("view".equals(action)) {    	 
                 // 处理查看宠物详情的请求
                 int petId = Integer.parseInt(request.getParameter("id"));
                 PetService petService = new PetService();
                 Pet pet = petService.getPetById(petId);
                 
                 if (pet != null) {
                     request.setAttribute("pet", pet);
                     RequestDispatcher dispatcher = request.getRequestDispatcher("pet_info.jsp");
                     dispatcher.forward(request, response);
                 } else {
                     request.setAttribute("errorMessage", "未找到宠物信息");
                     response.sendRedirect("PetServlet?action=adopthome.jsp");
                 }
         }
        
        //我的宠物----------
        else if ("viewMyPets".equals(action)) {
        	 // 登录状态检查
            if (request.getSession().getAttribute("userId") == null) {
                response.sendRedirect("log.jsp");  // 未登录，跳转到登录页面
                return;
            }
            Integer userId = (Integer) request.getSession().getAttribute("userId");
            // 获取用户上传的宠物
            List<Pet> uploadedPets = petService.getPetsByUserId(userId);
            // 获取用户领养的宠物
            List<Pet> adoptedPets = petService.getPetsByAdoptId(userId);

            request.setAttribute("uploadedPets", uploadedPets);
            request.setAttribute("adoptedPets", adoptedPets);
            
            // 跳转到用户的宠物页面
            request.getRequestDispatcher("user_pets.jsp").forward(request, response);
        }
        
        
        //查询宠物信息
        else if ("search".equals(action)) {
        	 String searchQuery = request.getParameter("searchQuery");
        	    List<Pet> pets;
        	    if (searchQuery != null && !searchQuery.trim().isEmpty()) {
        	        pets = petService.searchPetsByName(searchQuery.trim());
        	    } else {
        	        pets = petService.getAllPets();
        	    }
        	    request.setAttribute("pets", pets);
        	    request.setAttribute("searchQuery", searchQuery);
        	    request.getRequestDispatcher("pet_manage.jsp").forward(request, response);
          }
        
     // 获取待审核宠物列表和待确认领养宠物列表
        else if ("approveList".equals(action)) {
            // 获取所有待审批的宠物（status = "pending"）
            List<Pet> pendingPets = petService.getPendingPets();
            request.setAttribute("pendingPets", pendingPets);
            
            // 获取所有待确认领养的宠物（adopted = "undetermined"）
            List<Pet> adoptPendingPets = petService.getAdoptPendingPets();
            request.setAttribute("adoptPendingPets", adoptPendingPets);
            
            // 跳转到审核页面
            request.getRequestDispatcher("pet_approve.jsp").forward(request, response);
        }
    
        else if("adoptList".equals(action)) {
        	 // 获取所有待确认领养的宠物（adopted = "undetermined"）
            List<Pet> adoptPendingPets = petService.getAdoptPendingPets();
            request.setAttribute("adoptPendingPets", adoptPendingPets);
            
            // 跳转到审核页面
            request.getRequestDispatcher("pet_adopt.jsp").forward(request, response);
        }  
    }
}
