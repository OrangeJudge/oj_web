package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.zeroturnaround.zip.ZipUtil;
import play.Logger;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;
import utils.FileCopy;
import utils.Markup;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Entity
public class Problem extends Model {
    @Id
    public Long id;
    @Column(unique = true, nullable = false)
    public String slug = UUID.randomUUID().toString();
    public int status = 1; // 0 normal; 1 editing; 2 pending; 3 view only; 4 deleted;

    public String title;
    @Lob
    public String description;
    public String tags;
    public String source;

    public int timeLimit; // in ms. 0 for not specified.
    public int memoryLimit; // in MB. 0 for not specified.
    public boolean specialJudge;

    public String resourcesHash;

    public Date createTime = new Date();
    public Date lastModifyTime = new Date();

    @ManyToOne
    public User author;

    @ManyToOne
    public Contest contest;
    public boolean showInProblems = true;

    @JsonIgnore
    @OneToMany(mappedBy = "problem")
    public List<Solution> solutions;

    @JsonIgnore
    @OneToMany(mappedBy = "problem")
    public List<Discussion> discussions;

    @JsonIgnore
    @OneToMany(mappedBy = "problem")
    public List<ProblemFollower> followedBy;

    @JsonIgnore
    @OneToMany(mappedBy = "problem")
    public List<ProblemStar> starredBy;

    public static Finder<Long, Problem> find = new Finder<>(Long.class, Problem.class);

    public static Problem findBySlug(String slug) {
        return find.where().eq("slug", slug).findUnique();
    }

    public String getDescriptionHTML() {
        String html = Markup.fromMarkdown(description);
        html = html.replaceAll("<h1>(.*?)</h1>", "<h4>$1</h4>");
        html = html.replaceAll("<h2>(.*?)</h2>", "<h5>$1</h5>");
        html = html.replaceAll("<h3>(.*?)</h3>", "<h6>$1</h6>");
        html = html.replaceAll("__ASSETS__", "/problem/" + id + "/assets");
        return html;
    }

    public boolean isSolvedBy(User user) {
        // TODO: this step must be optimized.
        for (Solution solution : solutions) {
            if (solution.user.equals(user)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the resource folder of the problem.
     * @return Path to the problem's resource folder.
     */
    @JsonIgnore
    public Path getResourcesPath() {
        return Paths.get("upload/problem/" + id);
    }

    /**
     * Get the asset folder of the problem.
     * @return Path to the problem's asset folder.
     */
    @JsonIgnore
    public Path getAssetPath() {
        return Paths.get("upload/assets/" + id);
    }

    /**
     * This method will return the problem data in a JSON string, which is used
     * for output and input problem from systems.
     * @return A JSON string
     */
    public String toJson() {
        ObjectNode json = Json.newObject();
        json.put("title", title);
        json.put("description", description);
        json.put("tags", tags);
        json.put("source", source);
        json.put("timeLimit", timeLimit);
        json.put("memoryLimit", memoryLimit);
        json.put("specialJudge", specialJudge);
        json.put("resourcesHash", resourcesHash);
        return json.toString();
    }

    public static Problem fromJson(String jsonString) {
        JsonNode json = Json.parse(jsonString);
        Problem problem = new Problem();
        problem.title = json.get("title").asText();
        problem.description = json.get("description").asText();
        problem.tags = json.get("tags").asText();
        problem.source = json.get("source").asText();
        problem.timeLimit = json.get("timeLimit").asInt();
        problem.memoryLimit = json.get("memoryLimit").asInt();
        problem.specialJudge = json.get("specialJudge").asBoolean();
        problem.resourcesHash = json.get("resourcesHash").asText();
        return problem;
    }

    public File problemZipFile() throws IOException {
        String problemId = "problem_" + id;
        Logger.info("Create problem zip package named " + problemId);
        Path tempDirectory = Files.createTempDirectory(problemId);
        // Write problem data file.
        File problemFile = new File(tempDirectory.toFile(), "data.json");
        PrintWriter writer = new PrintWriter(problemFile, "UTF-8");
        writer.println(toJson());
        writer.close();
        // Copy files.
        FileCopy.copyDirectory(getResourcesPath(), tempDirectory.resolve("resources"));
        FileCopy.copyDirectory(getAssetPath(), tempDirectory.resolve("assets"));
        // Create zip package.
        File zip = File.createTempFile(problemId + "_", ".zip");
        ZipUtil.pack(tempDirectory.toFile(), zip);
        return zip;
    }

    public static Problem importZipFile(File importFile) throws IOException {
        Path tempDirectory = Files.createTempDirectory("imported_problem");
        Logger.info("Create temperary folder handle packed problem at " + tempDirectory.toAbsolutePath().toString() + ".");
        ZipUtil.unpack(importFile, tempDirectory.toFile());
        File problemFile = new File(tempDirectory.toFile(), "data.json");
        Logger.info("Read from problem file " + problemFile.getAbsolutePath() + ".");
        String problemJson = "";
        Scanner scanner = new Scanner(problemFile);
        while (scanner.hasNextLine()) {
            problemJson += scanner.nextLine() + "\n";
        }
        Problem problem = Problem.fromJson(problemJson);
        problem.save();
        Logger.info("Problem created with id " + problem.id);
        FileCopy.copyDirectory(tempDirectory.resolve("resources"), problem.getResourcesPath());
        FileCopy.copyDirectory(tempDirectory.resolve("assets"), problem.getAssetPath());
        return problem;
    }
}