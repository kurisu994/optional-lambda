package kurisu;

/**
 * @author MakiseKurisu
 * @date 2018-12-24 22:58
 */
public class SomeOne {
    private Long id;
    private Integer age;
    private Gender gender;
    private String name;
    private OneThing oneThing;

    public SomeOne(Long id, Integer age, Gender gender, String name) {
        this.id = id;
        this.age = age;
        this.gender = gender;
        this.name = name;
    }

    public SomeOne() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OneThing getOneThing() {
        return oneThing;
    }

    public void setOneThing(OneThing oneThing) {
        this.oneThing = oneThing;
    }

    public enum Gender {
        MALE, female
    }

    public class OneThing {
        private String thingName;
        private String use;

        public OneThing(String thingName, String use) {
            this.thingName = thingName;
            this.use = use;
        }

        public String getThingName() {
            return thingName;
        }

        public void setThingName(String thingName) {
            this.thingName = thingName;
        }

        public String getUse() {
            return use;
        }

        public void setUse(String use) {
            this.use = use;
        }
    }

    public OneThing createThing() {
        return new OneThing("123", "123456");
    }
}