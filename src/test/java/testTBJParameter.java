
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.cas.tbjhandler.Services.TBJControllers;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class testTBJParameter {

    static GRiderCAS poApp;
    static TBJControllers poTBJController;

    @BeforeClass
    public static void setUpClass() {
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/new/");

        poApp = MiscUtil.Connect();

        poTBJController = new TBJControllers(poApp, null);
    }
//    @Test
    public void testNewTransaction() {
        String SourceCode = "DISb";
        String CategoryID = "0000009";
        String Remarks = "Unit testing for tbj master and detail";
        
        String tableName = "Payment_Request_Master";
        String accountNo = "5101000";
        String acctType = "D";
        String sdervdFld = "nTranTotl";

        JSONObject loJSON;

        try {
            loJSON = poTBJController.TBJParameter().InitTransaction();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            loJSON = poTBJController.TBJParameter().NewTransaction();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
            
            poTBJController.TBJParameter().Master().setSourceCode(SourceCode); //direct assignment of value
            Assert.assertEquals(poTBJController.TBJParameter().Master().getSourceCode(), SourceCode);
            
            poTBJController.TBJParameter().Master().setCategoryID(CategoryID); //direct assignment of value
            Assert.assertEquals(poTBJController.TBJParameter().Master().getCategoryID(), CategoryID);

            poTBJController.TBJParameter().Master().setRemarks(Remarks);
            Assert.assertEquals( poTBJController.TBJParameter().Master().getRemarks(),Remarks);
            

            
            poTBJController.TBJParameter().Detail(0).setTableNm(tableName);
            poTBJController.TBJParameter().Detail(0).setAccountNo(accountNo);
            poTBJController.TBJParameter().Detail(0).setAccountType(acctType);
            poTBJController.TBJParameter().Detail(0).setDerivedField(sdervdFld);
            
            poTBJController.TBJParameter().AddDetail();
            System.out.println("MASTER TRANSACTION NO : " + poTBJController.TBJParameter().Master().getTransactionNo());
            System.out.println("DETAIL TRANSACTION NO : " + poTBJController.TBJParameter().Detail(0).getTransactionNo());
            
            loJSON = poTBJController.TBJParameter().SaveTransaction();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
        } catch (CloneNotSupportedException | SQLException | ExceptionInInitializerError | GuanzonException e) {
            System.err.println(MiscUtil.getException(e));
            Assert.fail();
        }
    }
    
    @Test
    public void testUpdateTransaction() {
        JSONObject loJSON;
        String SourceCode = "DISb";
        String CategoryID = "0000009";
        String Remarks = "Unit testing for tbj master and detail";
        
        String tableName = "Payment_Request_Master";
        String accountNo = "5101000";
        String acctType = "D";
        String sdervdFld = "nTranTotl";
        int lnCtr = 0;

        try {
            loJSON = poTBJController.TBJParameter().InitTransaction();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            
            loJSON = poTBJController.TBJParameter().OpenTransaction("GCO126000003");
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            loJSON = poTBJController.TBJParameter().UpdateTransaction();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
            
           
            for( lnCtr = 0;lnCtr <= poTBJController.TBJParameter().getDetailCount()-1; lnCtr++){
                System.out.println("DATA Before SAVE TRANSACTION Method");
                System.out.println("TransNo : " + (lnCtr+1) + " : " + poTBJController.TBJParameter().Detail(lnCtr).getTransactionNo());
                System.out.println("Source Code : " + (lnCtr+1) + " : " + poTBJController.TBJParameter().Detail(lnCtr).getAccountNo());
                System.out.println("Source No : " + (lnCtr+1) + " : " + poTBJController.TBJParameter().Detail(lnCtr).getAccountType());
                System.out.println("Debit Amount : " + (lnCtr+1) + " : " + poTBJController.TBJParameter().Detail(lnCtr).getDerivedField());
                System.out.println("---------------------------------------------------------------------");
            }
            
            poTBJController.TBJParameter().AddDetail();
             poTBJController.TBJParameter().Detail(lnCtr+1).setTableNm(tableName);
            poTBJController.TBJParameter().Detail(lnCtr+1).setAccountNo(accountNo);
            poTBJController.TBJParameter().Detail(lnCtr+1).setAccountType(acctType);
            poTBJController.TBJParameter().Detail(lnCtr+1).setDerivedField(sdervdFld);
            
            
            loJSON = poTBJController.TBJParameter().SaveTransaction();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
        } catch (CloneNotSupportedException | SQLException | GuanzonException e) {
            System.err.println(MiscUtil.getException(e));
            Assert.fail();
        } 

    }
    
    
    
    @AfterClass
    public static void tearDownClass() {
        poTBJController = null;
        poApp = null;
    }
    
    
}
