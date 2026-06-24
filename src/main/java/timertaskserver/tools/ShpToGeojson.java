package timertaskserver.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.geotools.geojson.geom.GeometryJSON;
import com.alibaba.fastjson.JSON;
import com.vividsolutions.jts.geom.*;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ShpToGeojson {
    public Map geojson2Shape(String jsonPath, String shpPath) {
        Map map = new HashMap();
        GeometryJSON gjson = new GeometryJSON();
        try {
            // 读文件到Stringbuffer
            StringBuffer sb = new StringBuffer();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(jsonPath));
                String str;
                while ((str = br.readLine()) != null) {// 逐行读取
                    sb.append(str + "\r\n");
                }
                br.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            JSONObject json = JSONObject.parseObject(sb.toString());

            JSONArray features = (JSONArray) json.get("features");
            JSONObject feature0 = JSONObject.parseObject(features.get(0).toString());
            // 获取属性名称
            Set properties = JSONObject.parseObject(feature0.getString("properties")).keySet();
            String strType = ((JSONObject) feature0.get("geometry")).getString("type").toString();

            Class<?> geoType = null;
            switch (strType) {
                case "Point":
                    geoType = Point.class;
                    break;
                case "MultiPoint":
                    geoType = MultiPoint.class;
                    break;
                case "LineString":
                    geoType = LineString.class;
                    break;
                case "MultiLineString":
                    geoType = MultiLineString.class;
                    break;
                case "Polygon":
                    geoType = Polygon.class;
                    break;
                case "MultiPolygon":
                    geoType = MultiPolygon.class;
                    break;
            }
            // 创建shape文件对象
            File file = new File(shpPath);
            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());
            ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
            // 定义图形信息和属性信息
            SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
            tb.setCRS(DefaultGeographicCRS.WGS84);
            tb.setName("shapefile");
            tb.add("the_geom", geoType);// 类型，Point/MultiPoint/LineString/MultiLineString/Polygon/MultiPolygon
            Iterator propertiesIter = properties.iterator();
            // 设置属性
            while (propertiesIter.hasNext()) {
                String str = propertiesIter.next().toString();
                tb.add(str, String.class);// 此处设置为string，如需修改请自行改写代码
            }

            ds.createSchema(tb.buildFeatureType());
            // 设置编码
            Charset charset = Charset.forName("GBK");
            ds.setCharset(charset);
            // 设置Writer
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(ds.getTypeNames()[0],
                    Transaction.AUTO_COMMIT);

            for (int i = 0, len = features.size(); i < len; i++) {
                String strFeature = features.get(i).toString();
                Reader reader = new StringReader(strFeature);
                SimpleFeature feature = writer.next();
                switch (strType) {
                    case "Point":
                        feature.setAttribute("the_geom", gjson.readPoint(reader));
                        break;
                    case "MultiPoint":
                        feature.setAttribute("the_geom", gjson.readMultiPoint(reader));
                        break;
                    case "LineString":
                        feature.setAttribute("the_geom", gjson.readLine(reader));
                        break;
                    case "MultiLineString":
                        feature.setAttribute("the_geom", gjson.readMultiLine(reader));
                        break;
                    case "Polygon":
                        feature.setAttribute("the_geom", gjson.readPolygon(reader));
                        break;
                    case "MultiPolygon":
                        feature.setAttribute("the_geom", gjson.readMultiPolygon(reader));
                        break;
                }
                Iterator propertiesset = properties.iterator();
                while (propertiesset.hasNext()) {
                    String str = propertiesset.next().toString();
                    JSONObject featurei = JSONObject.parseObject(features.get(i).toString());
                    feature.setAttribute(str, JSONObject.parseObject(featurei.getString("properties")).get(str));
                }
                writer.write();
            }
            writer.close();
            ds.dispose();
            map.put("status", "success");
            map.put("message", shpPath);
        } catch (Exception e) {
            map.put("status", "failure");
            map.put("message", e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    /**
     * shp文件转换geojson数据
     * @param shpPath
     * @return
     */
    public static Object shp2geojson(String shpPath)
    {
        Map map = new HashMap();
        //新建json对象
        FeatureJSON fjson = new FeatureJSON();
        JSONObject geojsonObject=new JSONObject();
        geojsonObject.put("type","FeatureCollection");
        try{
            //获取featurecollection
            File file = new File(shpPath);
            ShapefileDataStore shpDataStore = null;
            shpDataStore = new ShapefileDataStore(file.toURL());
            //设置编码
/*            Charset charset = Charset.forName("GBK");
            shpDataStore.setCharset(charset);*/
            String typeName = shpDataStore.getTypeNames()[0];
            SimpleFeatureSource featureSource = null;
            featureSource =  shpDataStore.getFeatureSource (typeName);
            SimpleFeatureCollection result = featureSource.getFeatures();
            SimpleFeatureIterator itertor = result.features();
            JSONArray array = new JSONArray();
            //遍历feature转为json对象
            while (itertor.hasNext())
            {
                SimpleFeature feature = itertor.next();
                StringWriter writer = new StringWriter();
                fjson.writeFeature(feature, writer);
                String temp=writer.toString();
                Object geometry = JSONObject.parseObject(temp).getString("geometry");
                byte[] b=temp.getBytes("iso8859-1");
                temp=new String(b,"gbk");
                JSONObject json =  JSON.parseObject(temp);
                array.add(geometry);
            }
            itertor.close();
            return array;
        }
        catch(Exception e){
            map.put("status", "failure");
            map.put("message", e.getMessage());
            e.printStackTrace();

        }
//
        return geojsonObject;
    }

    /**
     * 工具类测试方法
     *
     * @param args
     */
    public static void main(String[] args) {
        ShpToGeojson shpToGeojson = new ShpToGeojson();
        //long start = System.currentTimeMillis();
        String shpPath = "C:\\Users\\韩济泽\\Documents\\WeChat Files\\wxid_4zxb693e5qea22\\FileStorage\\File\\2023-09\\新北区现状水利工程\\新北区现状水利工程.shp";
        //String jsonPath = "E:\\a\\测试.geojson";
        Object geojson = shp2geojson(shpPath);

        System.out.println(geojson);
        //System.out.println(shpPath +" "+ jsonPath + ",共耗时" + (System.currentTimeMillis() - start) + "ms");
    }
}
