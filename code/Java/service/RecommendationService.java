package service;

import dao.*;
import model.*;

import java.util.*;
import java.util.stream.Collectors;

public class RecommendationService {
    private final PetSimilarityDao petSimilarityDao = new PetSimilarityDao();
    private final UserSimilarityDao userSimilarityDao = new UserSimilarityDao();
    private final PetDao petDao = new PetDao();
    private final FavoriteItemDao favoriteItemDao = new FavoriteItemDao();

    // 基于物品的协同过滤推荐
    public List<Pet> getItemBasedRecommendations(int userId) {
        try {
            List<Map<String, Object>> favoriteStats = favoriteItemDao.getFavoriteStats(userId);
            if (favoriteStats == null || favoriteStats.isEmpty()) return Collections.emptyList();

            Integer favoritePetId = (Integer) favoriteStats.get(0).get("pet_id");
            if (favoritePetId == null) return Collections.emptyList();

            List<PetSimilarity> similarPets = petSimilarityDao.getTopSimilarPets(favoritePetId, 5);

            return similarPets.stream()
                    .map(sim -> sim.getPetId1() == favoritePetId ? sim.getPetId2() : sim.getPetId1())
                    .distinct()
                    .map(petDao::getPetById)
                    .filter(Objects::nonNull)
                    .toList();

        } catch (Exception e) {
            // 建议替换为日志记录工具
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 基于用户的协同过滤推荐
    public List<Pet> getUserBasedRecommendations(int userId) {
        try {
            List<UserSimilarity> similarUsers = userSimilarityDao.getTopSimilarUsers(userId, 10);
            Map<Integer, Double> petScores = new HashMap<>();

            for (UserSimilarity sim : similarUsers) {
                int otherUserId = sim.getUserId1() == userId ? sim.getUserId2() : sim.getUserId1();
                List<Map<String, Object>> favorites = favoriteItemDao.getFavoriteStats(otherUserId);

                for (Map<String, Object> fav : favorites) {
                    int petId = (Integer) fav.get("pet_id");
                    if (!favoriteItemDao.isFavorite(userId, petId)) {
                        petScores.merge(petId, sim.getSimilarity(), Double::sum);
                    }
                }
            }

            return petScores.entrySet().stream()
                    .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                    .limit(5)
                    .map(entry -> petDao.getPetById(entry.getKey()))
                    .filter(Objects::nonNull)
                    .toList();

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 计算并更新所有宠物之间的相似度
    public void updatePetSimilarities() {
        try {
            List<Pet> pets = petDao.getAllPets();

            for (int i = 0; i < pets.size(); i++) {
                for (int j = i + 1; j < pets.size(); j++) {
                    Pet pet1 = pets.get(i);
                    Pet pet2 = pets.get(j);

                    double sim = calculatePetSimilarity(pet1, pet2);

                    PetSimilarity ps = new PetSimilarity();
                    ps.setPetId1(pet1.getId());
                    ps.setPetId2(pet2.getId());
                    ps.setSimilarity(sim);

                    petSimilarityDao.saveSimilarity(ps);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 计算并更新所有用户之间的相似度
    public void updateUserSimilarities() {
        try {
            // 使用0表示获取所有收藏数据
            List<Map<String, Object>> allFavorites = favoriteItemDao.getFavoriteStats(0);
            Map<Integer, Set<Integer>> userFavs = new HashMap<>();

            for (Map<String, Object> fav : allFavorites) {
                int userId = (Integer) fav.get("user_id");
                int petId = (Integer) fav.get("pet_id");
                userFavs.computeIfAbsent(userId, k -> new HashSet<>()).add(petId);
            }

            List<Integer> userIds = new ArrayList<>(userFavs.keySet());

            for (int i = 0; i < userIds.size(); i++) {
                for (int j = i + 1; j < userIds.size(); j++) {
                    int user1 = userIds.get(i), user2 = userIds.get(j);
                    double sim = calculateUserSimilarity(user1, user2, userFavs);

                    UserSimilarity us = new UserSimilarity();
                    us.setUserId1(user1);
                    us.setUserId2(user2);
                    us.setSimilarity(sim);

                    userSimilarityDao.saveSimilarity(us);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 计算两个宠物的相似度
    private double calculatePetSimilarity(Pet pet1, Pet pet2) {
        double score = 0.0;

        if (Objects.equals(pet1.getType(), pet2.getType())) score += 0.4;
        if (Objects.equals(pet1.getBreed(), pet2.getBreed())) score += 0.3;

        int ageDiff = Math.abs(pet1.getAge() - pet2.getAge());
        score += Math.max(0, 0.2 * (1 - ageDiff / 10.0));

        if (Objects.equals(pet1.getDescription(), pet2.getDescription())) score += 0.1;

        return score;
    }

    // 计算两个用户的Jaccard相似度
    private double calculateUserSimilarity(int uid1, int uid2, Map<Integer, Set<Integer>> userFavs) {
        Set<Integer> favs1 = userFavs.getOrDefault(uid1, Set.of());
        Set<Integer> favs2 = userFavs.getOrDefault(uid2, Set.of());

        if (favs1.isEmpty() || favs2.isEmpty()) return 0.0;

        Set<Integer> intersection = new HashSet<>(favs1);
        intersection.retainAll(favs2);

        Set<Integer> union = new HashSet<>(favs1);
        union.addAll(favs2);

        return (double) intersection.size() / union.size();
    }
}
