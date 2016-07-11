package com.mediamath.terminalone.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Form;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.mediamath.terminalone.Connection;
import com.mediamath.terminalone.Exceptions.ClientException;
import com.mediamath.terminalone.Exceptions.ParseException;
import com.mediamath.terminalone.models.Advertiser;
import com.mediamath.terminalone.models.Agency;
import com.mediamath.terminalone.models.AtomicCreative;
import com.mediamath.terminalone.models.Campaign;
import com.mediamath.terminalone.models.Concept;
import com.mediamath.terminalone.models.FieldError;
import com.mediamath.terminalone.models.JsonPostErrorResponse;
import com.mediamath.terminalone.models.JsonResponse;
import com.mediamath.terminalone.models.Organization;
import com.mediamath.terminalone.models.Pixel;
import com.mediamath.terminalone.models.Strategy;
import com.mediamath.terminalone.models.StrategyConcept;
import com.mediamath.terminalone.models.StrategyDayPart;
import com.mediamath.terminalone.models.StrategySupplySource;
import com.mediamath.terminalone.models.T1Entity;
import com.mediamath.terminalone.models.T1Error;
import com.mediamath.terminalone.models.T1Meta;
import com.mediamath.terminalone.models.T1Response;
import com.mediamath.terminalone.models.TOneASCreativeAssetsApprove;
import com.mediamath.terminalone.models.TOneASCreativeAssetsUpload;
import com.mediamath.terminalone.models.ThreePASCreativeBatchApprove;
import com.mediamath.terminalone.models.ThreePASCreativeUpload;
import com.mediamath.terminalone.models.helper.AdvertiserHelper;
import com.mediamath.terminalone.models.helper.AgencyHelper;
import com.mediamath.terminalone.models.helper.AtomicCreativeHelper;
import com.mediamath.terminalone.models.helper.CampaignHelper;
import com.mediamath.terminalone.models.helper.ConceptHelper;
import com.mediamath.terminalone.models.helper.OrganizationHelper;
import com.mediamath.terminalone.models.helper.PixelHelper;
import com.mediamath.terminalone.models.helper.StrategyConceptHelper;
import com.mediamath.terminalone.models.helper.StrategyHelper;
import com.mediamath.terminalone.models.helper.StrategySupplySourceHelper;
import com.mediamath.terminalone.models.helper.TOneCreativeAssetsApproveHelper;
import com.mediamath.terminalone.models.helper.ThreePasCreativeUploadBatchHelper;
import com.mediamath.terminalone.utils.Constants;
import com.mediamath.terminalone.utils.T1JsonToObjParser;

public class PostService {
	
	private static final Logger logger = LoggerFactory.getLogger(PostService.class);
	
	private static T1Service t1Service = new T1Service();
	
	private Connection connection = null; 
	
	//private HashMap<String, HashMap<String, String>> user = new HashMap<String, HashMap<String,String>>();
	private T1Response user = null;
	
	
	private static final String YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";
	
	public PostService() {
		// TODO Auto-generated constructor stub
	}
	
	public PostService(Connection pConnection, T1Response pUser) {
		this.connection = pConnection;
		this.user = pUser;
	}
	
	
	private <T extends T1Entity> StringBuffer getURI(T entity) {
		//detect
		String entityName = entity.getEntityname();
		//form the path
		StringBuffer uri = new StringBuffer(Constants.entityPaths.get(entityName));
		return uri;
	}

	public Agency save(Agency entity) throws ClientException, ParseException {
	
		Agency agency = null;
		
		if(entity != null) {
			JsonResponse<? extends T1Entity>  finalJsonResponse = null;
			
			StringBuffer uri = getURI(entity);
			
			if (entity.getId() > 0) {
				uri.append("/");
				uri.append(entity.getId());
			}
			
			String path = t1Service.constructURL(uri);

			String response = this.connection.post(path, AgencyHelper.getForm(entity), this.user);
			
			// parse response
			T1JsonToObjParser parser = new T1JsonToObjParser();
			if(!response.isEmpty()) {
				JsonPostErrorResponse error = jsonPostErrorResponseParser(response);
				if(error == null) {
					finalJsonResponse = parsePostData(response, parser, entity);
					if(finalJsonResponse != null && finalJsonResponse.getData() != null) {
						agency = (Agency) finalJsonResponse.getData();
					}
				} else {
					throwExceptions(error);
				}
			}
			
		}
		return agency;
	}

	private <T extends T1Entity> JsonResponse<? extends T1Entity> parsePostData(String response, T1JsonToObjParser parser, T entity ) 
	throws ParseException {
	
		// parse the string to gson objs
		JsonResponse<? extends T1Entity>  finalJsonResponse= null; 
		JsonElement element = parser.getDataFromResponse(response);
		if(element != null) {
			if(element.isJsonArray()) {
				// do something
				JsonArray jarray = element.getAsJsonArray();
				
				
			} else if (element.isJsonObject()) {
				JsonObject obj = element.getAsJsonObject();
				JsonElement entityTypeElement = obj.get("entity_type");
				String entityType =entityTypeElement.getAsString();
				//System.out.println(entityType);
				finalJsonResponse = parser.parseJsonToObj(response, Constants.getEntityType.get(entityType));
			}
		} else if(element == null) {
			finalJsonResponse = parser.parseJsonToObj(response, Constants.getEntityType.get(entity.getEntityname().toLowerCase()));
			if(finalJsonResponse != null) {
				finalJsonResponse.setData(null);
			}
		}
		return finalJsonResponse;
	}

	public Advertiser save(Advertiser entity) throws ClientException, ParseException {
		Advertiser advertiser = null;
		
		if(entity != null) {
			JsonResponse<? extends T1Entity>  finalJsonResponse = null;

			StringBuffer uri = getURI(entity);
			
			if (entity.getId() > 0) {
				uri.append("/");
				uri.append(entity.getId());
			}
			
			String path = t1Service.constructURL(uri);

			String response = this.connection.post(path, AdvertiserHelper.getForm(entity), this.user);
			
			// parse response
			T1JsonToObjParser parser = new T1JsonToObjParser();

			if(!response.isEmpty()) {
				JsonPostErrorResponse error = jsonPostErrorResponseParser(response);
				if(error == null) {
					finalJsonResponse = parsePostData(response, parser, entity);
					if(finalJsonResponse != null && finalJsonResponse.getData() != null) {
						advertiser = (Advertiser) finalJsonResponse.getData();
					}
				} else {
					throwExceptions(error);
				}
			}
			
		}
		return advertiser;
	}
	
	public Strategy save(Strategy entity) throws ClientException, ParseException {
		Strategy strategy = null;
		
		if(entity != null) {
			JsonResponse<? extends T1Entity>  finalJsonResponse = null;

			StringBuffer uri = getURI(entity);
			
			if (entity.getId() > 0) {
				uri.append("/");
				uri.append(entity.getId());
			}
			
			if (entity.getId() > 0 && entity.getDomain_restrictions().size() > 0) {
				uri.append("/domain_restrictions");
			}
			
			if (entity.getId() > 0 && entity.getAudience_segments().size() > 0 && entity.getAudience_segment_exclude_op()!=null && entity.getAudience_segment_include_op()!=null) {
				uri.append("/audience_segments");
			}
			
			String path = t1Service.constructURL(uri);

			String response = this.connection.post(path, StrategyHelper.getForm(entity), this.user);
			
			// parse response
			T1JsonToObjParser parser = new T1JsonToObjParser();

			if(!response.isEmpty()) {
				
				// parse error
				JsonPostErrorResponse error = jsonPostErrorResponseParser(response);
				if(error == null) {
					finalJsonResponse = parsePostData(response, parser, entity);
					if(finalJsonResponse != null && finalJsonResponse.getData() != null) {
						strategy = (Strategy) finalJsonResponse.getData();
					}
				} else {
					throwExceptions(error);
				}
			}
			
			
		}
		return ((strategy==null) ? entity : strategy);
	}
	
	public StrategyConcept save(StrategyConcept entity) throws ClientException, ParseException {
		StrategyConcept strategyConcept = null;
		
		if(entity != null) {
			JsonResponse<? extends T1Entity>  finalJsonResponse = null;

			StringBuffer uri = getURI(entity);
			
			if (entity.getId() > 0) {
				uri.append("/");
				uri.append(entity.getId());
			}
			
			String path = t1Service.constructURL(uri);

			String response = this.connection.post(path, StrategyConceptHelper.getForm(entity), this.user);
			
			// parse response
			T1JsonToObjParser parser = new T1JsonToObjParser();
		/*	JsonPostResponse jsonPostResponse =  null;
			
			jsonPostResponse = jsonPostErrorResponseParser(response);
			
			if(jsonPostResponse == null) {
				finalJsonResponse = parseData(response, parser);
				if(finalJsonResponse.getData() instanceof StrategyConcept) {
					strategyConcept = (StrategyConcept) finalJsonResponse.getData();
				}
			} else {
				throwExceptions(jsonPostResponse);
			}*/
			
			if(!response.isEmpty()) {
				JsonPostErrorResponse error = jsonPostErrorResponseParser(response);
				if(error == null) {
					finalJsonResponse = parsePostData(response, parser, entity);
					if(finalJsonResponse != null && finalJsonResponse.getData() != null) {
						strategyConcept = (StrategyConcept) finalJsonResponse.getData();
					}
				} else {
					throwExceptions(error);
				}
			}
			
		}
		return strategyConcept;
	}
	
	public StrategySupplySource save(StrategySupplySource entity) throws ClientException, ParseException {
		StrategySupplySource strategySupplySource = null;
		
		if(entity != null) {
			JsonResponse<? extends T1Entity>  finalJsonResponse = null;

			StringBuffer uri = getURI(entity);
			
			if (entity.getId() > 0) {
				uri.append("/");
				uri.append(entity.getId());
			}
			
			String path = t1Service.constructURL(uri);

			String response = this.connection.post(path, StrategySupplySourceHelper.getForm(entity), this.user);
			
			// parse response
			T1JsonToObjParser parser = new T1JsonToObjParser();

			if(!response.isEmpty()) {
				JsonPostErrorResponse error = jsonPostErrorResponseParser(response);
				if(error == null) {
					finalJsonResponse = parsePostData(response, parser, entity);
					if(finalJsonResponse != null && finalJsonResponse.getData() != null) {
						strategySupplySource = (StrategySupplySource) finalJsonResponse.getData();
					}
				} else {
					throwExceptions(error);
				}
			}
			
			
		}
		return strategySupplySource;
	}
	
	public Organization save(Organization entity) throws ClientException, ParseException {
		Organization org = null;
		
		if(entity != null) {
			JsonResponse<? extends T1Entity>  finalJsonResponse = null;

			StringBuffer uri = getURI(entity);
			
			if (entity.getId() > 0) {
				uri.append("/");
				uri.append(entity.getId());
			}
			
			String path = t1Service.constructURL(uri);

			String response = this.connection.post(path, OrganizationHelper.getForm(entity), this.user);
			
			// parse response
			T1JsonToObjParser parser = new T1JsonToObjParser();
			if(!response.isEmpty()) {
				JsonPostErrorResponse error = jsonPostErrorResponseParser(response);
				if(error == null) {
					finalJsonResponse = parsePostData(response, parser, entity);
					if(finalJsonResponse != null && finalJsonResponse.getData() != null) {
						org = (Organization) finalJsonResponse.getData();
					}
				} else {
					throwExceptions(error);
				}
			}
			
		}
		return org;
	}
	
	
	public Pixel save(Pixel entity) throws ClientException, ParseException {
		Pixel px = null;
		
		if(entity != null) {
			JsonResponse<? extends T1Entity>  finalJsonResponse = null;

			StringBuffer uri = getURI(entity);
			
			if (entity.getId() > 0) {
				uri.append("/");
				uri.append(entity.getId());
			}
			
			String path = t1Service.constructURL(uri);

			String response = this.connection.post(path, PixelHelper.getForm(entity), this.user);
			
			// parse response
			T1JsonToObjParser parser = new T1JsonToObjParser();

			if(!response.isEmpty()) {
				JsonPostErrorResponse error = jsonPostErrorResponseParser(response);
				if(error == null) {
					finalJsonResponse = parsePostData(response, parser, entity);
					if(finalJsonResponse != null && finalJsonResponse.getData() != null) {
						px = (Pixel) finalJsonResponse.getData();
					}
				} else {
					throwExceptions(error);
				}
			}
			
		}
		return px;
	}
	
	public Campaign save(Campaign entity) throws ParseException, ClientException {
		Campaign campaign = null;
		if (entity != null) {
			JsonResponse<? extends T1Entity> finalJsonResponse = null;

			StringBuffer uri = getURI(entity);

			if (entity.getId() > 0 && entity.getMargins().size() > 0) {
				uri.append("/");
				uri.append(entity.getId());
				uri.append("/margins");
			}
			String path = t1Service.constructURL(uri);
			// post
			String response = this.connection.post(path, CampaignHelper.getForm(entity), this.user);

			T1JsonToObjParser parser = new T1JsonToObjParser();

			if(!response.isEmpty()) {
				JsonPostErrorResponse error = jsonPostErrorResponseParser(response);
				if(error == null) {
					finalJsonResponse = parsePostData(response, parser, entity);
					if(finalJsonResponse != null && finalJsonResponse.getData() != null) {
						campaign = (Campaign) finalJsonResponse.getData();
					}
				} else {
					throwExceptions(error);
				}
			}
		}
		return campaign == null ? entity : campaign;
	}
	
	public Concept save(Concept entity) throws ParseException, ClientException {
		Concept concept = null;

		if (entity != null) {
			JsonResponse<? extends T1Entity> finalJsonResponse = null;

			StringBuffer uri = getURI(entity);

			String path = t1Service.constructURL(uri);
			// post
			String response = this.connection.post(path, ConceptHelper.getForm(entity), this.user);

			T1JsonToObjParser parser = new T1JsonToObjParser();

			if(!response.isEmpty()) {
				JsonPostErrorResponse error = jsonPostErrorResponseParser(response);
				if(error == null) {
					finalJsonResponse = parsePostData(response, parser, entity);
					if(finalJsonResponse != null && finalJsonResponse.getData() != null) {
						concept = (Concept) finalJsonResponse.getData();
					}
				} else {
					throwExceptions(error);
				}
				
			}
		}
		return concept == null ? entity : concept;
	}
	
	
	/**	Delete Strategy Concepts
	 * 
	 * @param strategyConcept
	 * @return
	 * @throws ClientException
	 * @throws ParseException
	 */
	public JsonResponse<? extends T1Entity> delete(StrategyConcept strategyConcept) throws ClientException, ParseException  {
		StringBuffer path = new StringBuffer();

		if(strategyConcept.getId() > 0){
				path.append(Constants.entityPaths.get("StrategyConcept"));
				path.append("/");
				path.append(strategyConcept.getId());
				path.append("/delete");
		}

		String finalPath = t1Service.constructURL(path);
		
		Form strategyConceptForm = new Form();
		
		String response  = connection.post(finalPath, strategyConceptForm, this.user);
		T1JsonToObjParser parser = new T1JsonToObjParser();
		JsonResponse<? extends T1Entity> jsonResponse = parser.parseJsonToObj(response, Constants.getEntityType.get("strategy_concepts"));
		
		
		return jsonResponse;
	}
	
	/**	Delete Strategy Day Parts
	 * 
	 * @param StrategyDayPart
	 * @return
	 * @throws ClientException
	 * @throws ParseException
	 */
	public JsonResponse<? extends T1Entity> delete(StrategyDayPart strategyDayPart) throws ClientException, ParseException  {
		StringBuffer path = new StringBuffer();

		if(strategyDayPart.getId() > 0){
				path.append(Constants.entityPaths.get("strategyDayPart"));
				path.append("/");
				path.append(strategyDayPart.getId());
				path.append("/delete");
		}

		String finalPath = t1Service.constructURL(path);
		
		Form strategyConceptForm = new Form();
		if(strategyDayPart.getVersion() > 0){
			strategyConceptForm.param("version", String.valueOf(strategyDayPart.getVersion()));
		}
		
		String response  = connection.post(finalPath, strategyConceptForm, this.user);
		T1JsonToObjParser parser = new T1JsonToObjParser();
		JsonResponse<? extends T1Entity> jsonResponse = parser.parseJsonToObj(response, Constants.getEntityType.get("strategy_day_parts"));
		
		
		return jsonResponse;
	}

	
	public AtomicCreative save(AtomicCreative entity) throws ParseException, ClientException {
		AtomicCreative atomicCreative = null;

		if (entity != null) {
			JsonResponse<? extends T1Entity> finalJsonResponse = null;

			StringBuffer uri = getURI(entity);

			String path = t1Service.constructURL(uri);
			// post
			String response = this.connection.post(path, AtomicCreativeHelper.getForm(entity), this.user);
			T1JsonToObjParser parser = new T1JsonToObjParser();

			if(!response.isEmpty()) {
				JsonPostErrorResponse error = jsonPostErrorResponseParser(response);
				if(error == null) {
					finalJsonResponse = parsePostData(response, parser, entity);
					if(finalJsonResponse != null && finalJsonResponse.getData() != null) {
						atomicCreative = (AtomicCreative) finalJsonResponse.getData();
					}
				} else {
					throwExceptions(error);
				}
			}
		}
		return atomicCreative == null ? entity : atomicCreative;
	}
	
	
	/**
	 * 
	 * @param filePath
	 * @param name
	 * @throws ClientException
	 * @throws IOException
	 */
	public ThreePASCreativeUpload save3pasCreativeUpload(String filePath, String fileName, String name) throws ClientException, IOException {
		
		ThreePASCreativeUpload threePassCreativeUploadResponse = null;
		
		if(filePath != null && name != null && fileName != null) {
			 
			// formt the url
			StringBuffer uri = new StringBuffer("creatives/upload");
			String path = t1Service.constructURL(uri);

			//form the data
			FileDataBodyPart filePart = new FileDataBodyPart("file", new File(filePath));
			FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
			final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("filename", fileName)
																	.field("name", name)
																	.bodyPart(filePart);
			
			String response = this.connection.post(path, multipart, this.user);
			//System.out.println(response);
			T1JsonToObjParser parser = new T1JsonToObjParser();
			
			// parse
			// create object and send the response to user.
			if(response != null && !response.isEmpty()) {
				threePassCreativeUploadResponse = parse3PasCreativeUploadData(response, parser);
			}
			
			formDataMultiPart.close();
			multipart.close();
		} else {
			throw new ClientException("please enter a valid filename and file path");
		}
		
		return threePassCreativeUploadResponse;
	}
	
	/**
	 * parser for 3PAS creative upload.
	 * 
	 * @param response
	 * @param parser
	 * @return
	 */
	private ThreePASCreativeUpload parse3PasCreativeUploadData(String response, T1JsonToObjParser parser) {
		ThreePASCreativeUpload finalResponse = null;
		finalResponse = parser.parse3PasCreativeUploadResponseTOObj(response);
		return finalResponse;
	}
	
	
	/**
	 * handles second call for 3PAS Creative Upload
	 * @param entity
	 * @throws ClientException
	 * @throws IOException
	 */
	//TODO work on return type, create a dto, fix the parser for valid response.
	@SuppressWarnings("unused")
	public void save3pasCreativeUploadBatch(ThreePASCreativeBatchApprove entity) throws ClientException, IOException {
		FormDataMultiPart formData = new FormDataMultiPart();

		if (entity != null) {
			
			StringBuffer uri = new StringBuffer("creatives/upload/");
			
			
			if(entity.getBatchId() != null && !entity.getBatchId().isEmpty()) {
				uri.append(entity.getBatchId());

				String path = t1Service.constructURL(uri);
				//TODO remove.
				//System.out.println(path);
				
				ThreePasCreativeUploadBatchHelper.getMultiPartForm(entity, formData);
				
				String response = this.connection.post(path, formData, this.user);
				
				//System.out.println("response: " + response);
				
				T1JsonToObjParser parser = new T1JsonToObjParser();
				JsonPostErrorResponse jsonPostResponse = null;
				
				jsonPostResponse = jsonPostErrorResponseParser(response);
				
				if (jsonPostResponse == null) {
					//System.out.println("COMMENTED DUE TO CLIENT EXCEPTION - ACCESS DENIED.");
					/*	// update the existing object. or create new object.
					//parseData(response, parser);
	
					if (finalJsonResponse.getData() instanceof AtomicCreative) {
						atomicCreative = (AtomicCreative) finalJsonResponse.getData();
					}
				 */
				} else {
					throwExceptions(jsonPostResponse);
				}
			}
		}
		
		if(formData != null) {
			formData.close();
		}
			
	}

	/**
	 * saves T1AS Creative Assets
	 * 
	 * @param filePath
	 * @param fileName
	 * @param name
	 * @return 
	 * @throws ClientException 
	 * @throws IOException 
	 */
	public TOneASCreativeAssetsUpload saveT1asCreativeAssets(String filePath, String fileName, String name) throws ClientException, IOException {
		TOneASCreativeAssetsUpload assetsUploadResponse = null;
		if(filePath != null && name != null && fileName != null) {
			 
			// formt the url
			StringBuffer uri = new StringBuffer("creative_assets/upload");
			String path = t1Service.constructURL(uri);

			//form the data
			FileDataBodyPart filePart = new FileDataBodyPart("file", new File(filePath));
			FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
			final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("filename", fileName)
																	.field("name", name)
																	.bodyPart(filePart);
			
			String response = this.connection.post(path, multipart, this.user);
			//System.out.println(response);
			T1JsonToObjParser parser = new T1JsonToObjParser();
			
			// parse
			// create object and send the response to user.
			if(response != null && !response.isEmpty()) {
				assetsUploadResponse = parseTOneASCreativeAssetsUploadData(response, parser);
			}
			
			formDataMultiPart.close();
			multipart.close();
		} else {
			throw new ClientException("please enter a valid filename and file path");
		}
		return assetsUploadResponse;
	}
	
	
	private TOneASCreativeAssetsUpload parseTOneASCreativeAssetsUploadData(String response, T1JsonToObjParser parser) {
		TOneASCreativeAssetsUpload finalResponse = null;
		finalResponse = parser.parseTOneASCreativeAssetsUploadResponseTOObj(response);
		return finalResponse;
	}
	
	/**
	 * handles second call for T1AS Creative Assets
	 * @param entity
	 * @throws ClientException 
	 */
	public JsonResponse<? extends T1Entity> saveTOneASCreativeAssetsApprove(TOneASCreativeAssetsApprove entity) throws ClientException {
		FormDataMultiPart formData = new FormDataMultiPart();
		//TOneASCreativeAssetsApproveResponse response = null;
		JsonResponse<? extends T1Entity> parsedJsonResponse = null;
		if (entity != null) {
			
			
			StringBuffer uri = new StringBuffer("creative_assets/approve");
			
			String path = t1Service.constructURL(uri);
			
			//System.out.println(path);
			
			TOneCreativeAssetsApproveHelper.getMultiPartForm(entity, formData);
			
			String jsonResponse = this.connection.post(path, formData, this.user);
			
			//System.out.println("response: " + jsonResponse);
			
			T1JsonToObjParser parser = new T1JsonToObjParser();
			JsonPostErrorResponse jsonPostErrorResponse = null;
			
			jsonPostErrorResponse = jsonPostErrorResponseParser(jsonResponse);
			
			if (jsonPostErrorResponse == null) {
				
				parsedJsonResponse = parser.parseTOneASCreativeAssetsApproveResponse(jsonResponse);
				/*if (parsedJsonResponse.getData() instanceof TOneASCreativeAssetsApproveResponse) {
					response = (TOneASCreativeAssetsApproveResponse) parsedJsonResponse.getData();
				}*/
				
			} else {
				throwExceptions(jsonPostErrorResponse);
			}
		}
		return parsedJsonResponse;
	}
	
	/**
	 * @param responseStr
	 */
	private JsonPostErrorResponse jsonPostErrorResponseParser(String responseStr) {
		JsonParser parser1 = new JsonParser();
		JsonObject obj = parser1.parse(responseStr).getAsJsonObject();
		
		JsonElement errorsElement = obj.get("errors");
		JsonElement errorElement = obj.get("error");
		JsonElement metaElement = obj.get("meta");
		
		JsonPostErrorResponse response = null;
		
		if(errorsElement != null || errorElement != null ) {
			response = new JsonPostErrorResponse();

			GsonBuilder builder = new GsonBuilder();
			builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES);
			builder.setDateFormat(YYYY_MM_DD_T_HH_MM_SS);
			
			Gson g = builder.create();

			if (errorsElement != null) {
				if (errorsElement.isJsonNull()) {

				} else if (errorsElement.isJsonObject()) {
					T1Error errors = g.fromJson(errorsElement, T1Error.class);
					response.setErrors(errors);

				} else if (errorsElement.isJsonArray()) {
					JsonArray array = errorsElement.getAsJsonArray();
					JsonArray newArray = new JsonArray();
					
					for(int i = 0; i < array.size(); i++) {
						if(!(array.get(i) instanceof JsonPrimitive)) {
							newArray.add(array.get(i));
							
						}
					}
					if(newArray.size() > 0) {
						errorsElement = newArray;
						Type t =  new TypeToken<ArrayList<T1Error>>(){}.getType();
						List<T1Error> errors = g.fromJson(errorsElement, t);
						response.setErrors(errors);
					}
				}
			}

			if (errorElement != null) {
				T1Error error = g.fromJson(errorElement, T1Error.class);
				response.setError(error);
			}

			if(metaElement != null) {
				T1Meta meta = g.fromJson(metaElement, T1Meta.class);
				response.setMeta(meta);
			}
			
		
		}
		
		return response;
	}
	
	/**
	 * @param jsonPostResponse
	 * @throws ClientException
	 */
	private void throwExceptions(JsonPostErrorResponse jsonPostResponse) throws ClientException {
	
		StringBuffer strbuff = null;

		if (jsonPostResponse.getError() != null) {
			T1Error error = jsonPostResponse.getError();

			if (error.getContent() != null) {
				strbuff = new StringBuffer("Content: " + error.getContent());
			}

			if (error.getField() != null) {
				if (strbuff == null) {
					strbuff = new StringBuffer("Field: " + error.getField());
				} else {
					strbuff.append(", " + "Field: " + error.getField());
				}
			}

			if (error.getMessage() != null) {
				if (strbuff == null) {
					strbuff = new StringBuffer("Message: " + error.getMessage());
				} else {
					strbuff.append(", " + "Message: " + error.getMessage());
				}
			}

			if (error.getType() != null) {
				if (strbuff == null) {
					strbuff = new StringBuffer("Type: " + error.getType());
				} else {
					strbuff.append(", " + "Type: " + error.getType());
				}
			}
		}

		if (jsonPostResponse.getErrors() != null) {
			if (jsonPostResponse.getErrors() instanceof ArrayList) {
				@SuppressWarnings("unchecked")
				ArrayList<T1Error> al = (ArrayList<T1Error>) jsonPostResponse.getErrors();
				for (T1Error error : al) {
					if (error.getMessage() != null) {
						if (strbuff == null) {
							strbuff = new StringBuffer(error.getMessage()); //add error field
						} else {
							strbuff.append(", " + error.getMessage());
						}
					}
					if (error.getFieldError() != null) {
						for (FieldError fe : error.getFieldError()) {
							if (strbuff == null) {
								strbuff = new StringBuffer("Name: " + fe.getName() + ", Code: " + fe.getCode()
										+ ", Error: " + fe.getError());
							} else {
								strbuff.append(", " + "Name: " + fe.getName() + ", Code: " + fe.getCode()
										+ ", Error: " + fe.getError());
							}
						}
					}
				}
			} else {

				T1Error error = (T1Error) jsonPostResponse.getErrors();

				if (error.getMessage() != null) {
					if (strbuff == null) {
						strbuff = new StringBuffer(error.getMessage());
					} else {
						strbuff.append(", " + error.getMessage());
					}
				}
				if (error.getFieldError() != null) {
					for (FieldError fe : error.getFieldError()) {
						if (strbuff == null) {
							strbuff = new StringBuffer("Name: " + fe.getName() + ", Code: " + fe.getCode()
									+ ", Error: " + fe.getError());
						} else {
							strbuff.append(", " + "Name: " + fe.getName() + ", Code: " + fe.getCode()
									+ ", Error: " + fe.getError());
						}
					}
				}
			}
		}
		// throw the error to client
		throw new ClientException(strbuff.toString());
	}




	
}