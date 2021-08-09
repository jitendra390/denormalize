package com.umg.denormalize.options;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;


public interface DataDenormalizeOptions extends PipelineOptions {

	@Description("Butcket Name ")
	  @Default.String("gcp-umg-spot-landing")
	  String getLandingBucketName();

	  void setLandingBucketName(String landingbucketname);
	  
	  
	  @Description("File Names in , seperated")
	  @Default.String("streams.gz,tracks.gz,users.gz")
	  String getFileNames();

	  void setFileNames(String filenames);
	  
	  
	  @Description("Output FileName")
	  @Default.String("streams_denorm.json")
	  String getOutputFileName();

	  void setOutputFileName(String outputfilename);
	  
	  @Description("Output bucket")
	  @Default.String("gcp-umg-spot-output")
	  String getOutPutBucketName();

	  void setOutPutBucketName(String outputbucketname);
}
