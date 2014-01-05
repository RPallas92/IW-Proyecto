package server;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.ArrayList;

import mail.Mail;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import pdf.PDF;

public class MonitoringThread implements Runnable{

	private int id;
	private String url;
	private Date startDate;
	private int frequency;
	private Date endDate;
	
	public MonitoringThread(){
		
	}
	
	public MonitoringThread(int id, String url, int frequency, Date endDate) throws IOException, NoSuchAlgorithmException{
		this.id = id;
		this.url = url;
		this.startDate = new Date(System.currentTimeMillis());
		this.frequency = frequency;
		this.endDate = endDate;		
		
		// Get the HTML of the page
		HTMLPage html = new HTMLPage(url);
	    
	    // Hash
		String hash = html.getHash();
		
		// Write into BD
		new Facade().insert(id, url, frequency, endDate, hash);
		
	}
	
	public void run() {
        try {
			 // First we must get a reference to a scheduler
	        SchedulerFactory sf = new StdSchedulerFactory();
	        Scheduler sched = sf.getScheduler();
	
	        // define the job and tie it to our MonitorinJob class
	        JobDetail job = newJob(MonitoringJob.class)
	            .withIdentity("job"+id, "monitoring")
	            .usingJobData("id", id)
	            .usingJobData("url", url)
	            .build();
	        
	        // Trigger the job to run on the next round minute
	        Trigger trigger = newTrigger()
	            .withIdentity("trigger"+id, "monitoring")
	            .withSchedule(simpleSchedule()
	            .withIntervalInSeconds(frequency)
	            .repeatForever())
	            .endAt(endDate)
	            .build();
	        
	        // Tell quartz to schedule the job using our trigger
	        sched.scheduleJob(job, trigger);
	
	        // Start up the scheduler (nothing can actually run until the 
	        // scheduler has been started)
	        sched.start();
	        
	        try {
				Thread.sleep(endDate.getTime()-startDate.getTime()+10000);
			} 
	        catch (InterruptedException e) {
				e.printStackTrace();
			} 
	        // Generate PDF
	        ArrayList<String> changes = new Facade().getChanges(id);
	        int numberOfChanges = getNumberOfCHanges(changes);
	        String pdfName = "informe_"+id+".pdf";
	        PDF pdf = new PDF(pdfName, url, startDate.toString(), endDate.toString(), Integer.toString(frequency), 
	        		"cjperez8086@gmail.com", Integer.toString(numberOfChanges), changes);
	        pdf.generatePDF();
	        
	        // Send mail
	        Mail mail = new Mail(url, "cjperez8086@gmail.com", pdfName);
	        mail.sendMail();
	        // shut down the scheduler
	        sched.shutdown(true);
		} 
        catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	private int getNumberOfCHanges (ArrayList<String> changes){
		int count = 0;
		for (String row : changes) {
			String[] splitStr = row.split("\\s+");
			
			if(splitStr[1].equals("Si")){
				count++;
			}
			
					
	    }
		return count;
	}
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException{
		Date s = new Date(System.currentTimeMillis()+305000);
		MonitoringThread m = new  MonitoringThread(9, "http://localhost:9999/sslist/login.html", 60, s);	
		Thread r = new Thread(m); 
		r.start();
	}
}
