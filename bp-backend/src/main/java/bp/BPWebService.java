package bp;


import java.io.Console;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bson.Document;
import org.springframework.util.StringUtils;

/**
 *
 * @author kanolan
 */
@Stateless
@Path("/bp")
public class BPWebService {
    
    @EJB
    private BloodPressure bloodPressure;
    private static ResourceBundle rb = ResourceBundle.getBundle("config");
   
    /**
     * Retrieves representation of an instance of bp.BPWebService
     * @return an instance of java.lang.String
     */
    @GET  //GET defines a reading access of the resource without side-effects
    @Produces(MediaType.TEXT_HTML)
    public String getHtmlBPCategory() {   
        try {
            //@PathParam("systolic") int systolic
            System.out.println("getHtmlBPCategory");
            bloodPressure = new BloodPressure(100, 80);
            return "<html><body><h1>You have "+bloodPressure.getBPCategory() +"! blood pressure </h1></body></html>";
        } catch (Exception e) {
            Logger.getLogger(BPWebService.class.getName()).log(Level.WARNING, null, e);
            return e.getMessage();
        }
    }
    
    // This method is called if JSON is request
    @Path("/getJson/{systolic}/{diastolic}/{email}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJSONBPCategory(@PathParam("systolic") int systolic, @PathParam("diastolic") int diastolic, @PathParam("email") String email) {
        Response response = null;
        try {
            System.out.println("getJSONBPCategory for: " + systolic + " - " + diastolic);   
            
            //Calculate the Blood Pressure Category
            bloodPressure = new BloodPressure(systolic, diastolic);
            
            //Save that to the Mongo DB
            if( !StringUtils.isEmpty(email)) {
                saveNewReading(email, systolic, diastolic, bloodPressure.getBPCategory().toString());
            }
            
            // Create Json and serialize
            JsonObject json = Json.createObjectBuilder()
            .add("systolic", systolic)
            .add("diastolic", diastolic)
            .add("category", bloodPressure.getBPCategory().toString()).build();
            
            response = Response.status(Response.Status.OK).entity(json.toString()).build();
        } catch (Exception e) {
            Logger.getLogger(BPWebService.class.getName()).log(Level.INFO, null, e.getMessage());
            //throw new WebApplicationException(Response.Status.NOT_FOUND);           
            response = Response.status(Response.Status.REQUESTED_RANGE_NOT_SATISFIABLE).entity("{\"error\":\"" + e.getMessage()+"\"}").build();
        }
        return response;
    }

    @Path("/version")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String returnVersion() {
 
        String  value= "the version number is  " + rb.getString("version");
        System.out.println("ran the version number");
    
 
        return value;
    }
    
    private void saveNewReading(String email, int systolic, int diastolic, String category) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            
            BPPersistence db = new BPPersistence();
            db.initiliaze();
            
            // make a document and insert it
            Document doc = new Document("email", email)
                   .append("systolic", systolic)
                   .append("diastolic", diastolic)
                   .append("category", category)
                   .append("date", format.format(new Date()));
            
            db.insertDocument(doc);
        } catch (Exception e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
       }
    }
  /**  
    // This method is called if TEXT_PLAIN is request
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getPlainTextBPCategory() {   
        bloodPressure = new BloodPressure(100, 80);
        return "You have "+bloodPressure.getBPCategory() +"! blood pressure";
    }
    
    // This method is called if TEXT_PLAIN is request
    @GET
    @Produces(MediaType.TEXT_XML)
    public String getXMLBPCategory() {   //@PathParam("systolic") int systolic
        bloodPressure = new BloodPressure(100, 80);
        return "<?xml version=\"1.0\"?>" + "<BPCategory>"+bloodPressure.getBPCategory() +"</BPCategory>";
    }
   */ 
}
