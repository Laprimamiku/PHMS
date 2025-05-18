package model;

public class Pet {
    private int id;
    private String name;
    private String type;
    private String breed;
    private int age;
    private String description;
    private String image_url;
    private String status;
    private String adopted;
    private int user_id;
    private int adopt_id;

    public int getAdopt_id() {
		return adopt_id;
	}

	public void setAdopt_id(int adopt_id) {
		this.adopt_id = adopt_id;
	}

	public String getAdopted() {
		return adopted;
	}

	public void setAdopted(String adopted) {
		this.adopted = adopted;
	}

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
