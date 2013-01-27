package org.syncany.experimental.db;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.syncany.util.FileLister;
import org.syncany.util.FileUtil;
import org.syncany.util.StringUtil;
import org.syncany.util.chunk.chunking.Chunk;
import org.syncany.util.chunk.chunking.Chunker;
import org.syncany.util.chunk.chunking.FixedOffsetChunker;
import org.syncany.util.chunk.multi.CustomMultiChunker;
import org.syncany.util.chunk.multi.MultiChunk;
import org.syncany.util.chunk.multi.MultiChunker;

public class Main {

	
	public static void main(String[] args) throws IOException {
		Database db;
		
		System.out.println("save1()");
		System.out.println("----------------------------");
		save1();
		
		System.out.println("load1()");
		System.out.println("----------------------------");
		db = load1();
		
		System.out.println("save2()");
		System.out.println("----------------------------");
		save2(db);

		System.out.println("save3()");
		System.out.println("----------------------------");
		save3();		

		System.out.println("load3()");
		System.out.println("----------------------------");
		load3();
	}
	
	public static Database save1() throws IOException {
		Database db = new Database();
		
		// Create first file
        ChunkEntry chunkA1 = db.createChunk(new byte[] { 1,2,3,4,5,7,8,9,0}, 12,true);
        ChunkEntry chunkA2 = db.createChunk(new byte[] { 9,8,7,6,5,4,3,2,1}, 34, true);
        ChunkEntry chunkA3 = db.createChunk(new byte[] { 1,1,1,1,1,1,1,1,1}, 56, true);
        ChunkEntry chunkA4 = db.createChunk(new byte[] { 2,2,2,2,2,2,2,2,2}, 78, true);
		
        Content contentA = db.createContent();        
        contentA.addChunk(chunkA1);
        contentA.addChunk(chunkA2);
        contentA.addChunk(chunkA3);
        contentA.addChunk(chunkA4);
        contentA.setChecksum(new byte[]{5,5,5,4,4,5,5,5,5});              
        db.addContent(contentA);
        
        FileHistory fileA = db.createFileHistory(true);
        
        FileVersion versionA1 = db.createFileVersion(fileA);
        versionA1.setPath("some/path");
        versionA1.setName("fileA.jpg");
        versionA1.setContent(contentA);
       // fileA.addVersion(versionA1);
        
        FileVersion versionA2 = db.createFileVersion(fileA);
        versionA2.setName("renamed-fileA.jpg");        
        //fileA.addVersion(versionA2);
        
        db.addFileHistory(fileA);
        
        // Create second file
        ChunkEntry chunkB1 = db.createChunk(new byte[] { 3,3,3,3,3,3,3,3,3}, 910, true);
        ChunkEntry chunkB2 = db.createChunk(new byte[] { 4,4,4,4,4,4,4,4,4}, 1112, true);
        
        Content contentB = db.createContent();
        contentB.addChunk(chunkB1);
        contentB.addChunk(chunkB2);   
        contentB.setChecksum(new byte[]{1,1,1,3,3,5,5,5,5});                      
        db.addContent(contentB);

        FileHistory fileB = db.createFileHistory(true);
        
        FileVersion versionB1 = db.createFileVersion(fileB);
        versionB1.setPath("some/other/path");
        versionB1.setName("fileB.jpg");
        versionB1.setContent(contentB);
        //fileB.addVersion(versionB1);
        
        FileVersion versionB2 = db.createFileVersion(fileB);
        versionB2.setName("renamed-fileB.jpg");        
        //fileB.addVersion(versionB2);
        
        db.addFileHistory(fileB);
        
        // Distribute chunks to multichunks
        MultiChunkEntry multiChunkA = db.createMultiChunk();
        multiChunkA.addChunk(chunkA1); // from fileA
        multiChunkA.addChunk(chunkA2); // from fileA
        multiChunkA.addChunk(chunkA3); // from fileA
        multiChunkA.setChecksum(new byte[] {6,6,6,6,6,6,6,6,6});
        db.addMultiChunk(multiChunkA);
        
        MultiChunkEntry multiChunkB = db.createMultiChunk();
        multiChunkB.addChunk(chunkA4); // from fileA
        multiChunkB.addChunk(chunkB1); // from fileB
        multiChunkB.addChunk(chunkB2); // from fileB
        multiChunkB.setChecksum(new byte[] {7,7,7,7,7,7,7,7,7});
        db.addMultiChunk(multiChunkB);

        // Save database
        File dbFile = new File("/tmp/dbfile");
        db.save(dbFile, true, false);        
        
        System.out.println(db);
        return db;
	}
	
	public static Database load1() throws IOException {
		Database db = new Database();
		
		// Load database
        File dbFile = new File("/tmp/dbfile");
		db.load(dbFile, false);

        System.out.println(db);
        
        return db;
	}
	
	public static Database save2(Database db) throws IOException {
		// Create first file
        ChunkEntry chunkC1 = db.createChunk(new byte[] { 99,92,93,4,5,7,8,9,0}, 912,true);
        ChunkEntry chunkC2 = db.createChunk(new byte[] { 99,98,97,6,5,4,3,2,1}, 934, true);
		
        Content contentC = db.createContent();        
        contentC.addChunk(chunkC1);
        contentC.addChunk(chunkC2);
        contentC.setChecksum(new byte[]{95,95,95,4,4,5,5,5,5});              
        db.addContent(contentC);
        
        FileHistory fileC = db.createFileHistory(true);
        
        FileVersion versionC1 = db.createFileVersion(fileC);
        versionC1.setPath("some/path");
        versionC1.setName("fileC.jpg");
        versionC1.setContent(contentC);
                
        db.addFileHistory(fileC);
        
        // Save new stuff
        File dbFile2 = new File("/tmp/dbfile2");
        db.save(dbFile2, false, false);

        System.out.println(db);
        return db;
	}
	
	public static void save3() throws IOException {
		// Create test environment
		File rootDir = new File("/tmp/syncany-db-test");
		File repoDir = new File("/tmp/syncany-db-test-repo");
		File localDatabaseFile = new File(repoDir+"/db-philipp-"+(System.currentTimeMillis()/1000));
		
		if (rootDir.exists()) {
			FileUtil.deleteRecursively(rootDir);
		}
		
		if (repoDir.exists()) {
			FileUtil.deleteRecursively(rootDir);
		}		
		
		rootDir.mkdir();
		repoDir.mkdir();
		
		// Make lots of random files and folders
		int maxFiles = 100;
		
		List<File> randomFiles = new ArrayList<File>();
		List<File> randomDirs = new ArrayList<File>();
		File currentDir = rootDir;
		
		for (int i=0; i<maxFiles; i++) {
			if (!randomDirs.isEmpty()) {
				currentDir = randomDirs.get((int) Math.random()*randomDirs.size());
			}

			if (Math.random() > 0.3) {
				File newFile = new File(currentDir+"/file"+i);
				FileUtil.createRandomFile(newFile, 1000, 500000);
				
				randomFiles.add(newFile);
			}
			else {
				currentDir = new File(currentDir+"/file"+i);
				currentDir.mkdir();
				
				randomDirs.add(currentDir);
				randomFiles.add(currentDir);
			}
		}
		
		// Now copy some files (1:1 copy), and slightly change some of them (1:0.9) 
		for(int i=maxFiles; i<maxFiles+maxFiles/4; i++) {
			File srcFile = randomFiles.get((int) (Math.random()*(double)randomFiles.size()));
			File destDir = randomDirs.get((int) (Math.random()*(double)randomDirs.size()));			
			
			if (srcFile.isDirectory()) {
				continue;
			}
			
			// Alter some of the copies (change some bytes)
			if (Math.random() > 0.5) {
				File destFile = new File(destDir+"/file"+i+"-almost-the-same-as-"+srcFile.getName());
				FileInputStream fis = new FileInputStream(srcFile);
				FileOutputStream fos = new FileOutputStream(destFile);
				
				byte[] buffer = new byte[4096];				

		        int len;
		        while ((len = fis.read(buffer)) > 0) {
		        	// Change 10% of the file
		        	if (Math.random() < 0.5) {
		        		//buffer[0] = (byte) (buffer[0]+1); // CHANGE SOMETHING!
		        		fos.write(buffer, 0, len-1);
		        	}
		        	
		        	// Or just copy stuff
		        	else {
		        		fos.write(buffer, 0, len);
		        	}
		        }

		        fis.close();
		        fos.close();
			}
			
			// Or simply copy them
			else {
				File destFile = new File(destDir+"/file"+i+"-copy-of-"+srcFile.getName());
				FileUtil.copy(srcFile, destFile);
			}
		}
		
		
		// NOW START CHUNKING, AND ADDING EVERYTHING TO THE DB
		
		// DB: Create DB and chunk/multichunk entries
		Database db = new Database();		
		
		ChunkEntry chunkEntry = null;
		MultiChunkEntry multiChunkEntry = null;

		// Data: Create chunker and multichunker
		Chunker chunker = new FixedOffsetChunker(4*1024);
		MultiChunker multiChunker = new CustomMultiChunker(512*1024, 0);
		
		Chunk chunk = null;
		MultiChunk multiChunk = null;
		
		for (File file : randomFiles) {
			// Check if file exists, or create new 
			FileHistory fileHistory = db.getFileHistory(FileUtil.getRelativePath(rootDir, file), file.getName());
			
			if (fileHistory == null) {
				fileHistory = db.createFileHistory();
			}
			
			// Check for versions
			FileVersion fileVersion = fileHistory.getLastVersion();
			
			if (fileVersion == null) {
				fileVersion = db.createFileVersion(fileHistory);
			}
			
			fileVersion.setPath(FileUtil.getRelativePath(rootDir, file));
			fileVersion.setName(file.getName());
			
			if (file.isFile()) {
				Content content = db.createContent();			
				
				// Create chunks from file
				Enumeration<Chunk> chunks = chunker.createChunks(file);
				
				while (chunks.hasMoreElements()) {
					chunk = chunks.nextElement();
					
					// Update DB
					chunkEntry = db.getChunk(chunk.getChecksum());
					
					if (chunkEntry == null) {
						chunkEntry = db.createChunk(chunk.getChecksum(), chunk.getSize(), true);						
						
						// Add chunk data to multichunk
						
						// - Check if multichunk full
						if (multiChunk != null && multiChunk.isFull()) {
							// Data
		                    multiChunk.close();
		                    multiChunk = null;
		                    
		                    // DB
		                    db.addMultiChunk(multiChunkEntry);
		                    multiChunkEntry = null;
		                }
						
						// - Open new multichunk if none existant
						if (multiChunk == null) {
							// Data
							File multiChunkFile = new File(repoDir+"/multichunk-"+StringUtil.toHex(chunk.getChecksum()));							
							multiChunk = multiChunker.createMultiChunk(chunk.getChecksum(), new FileOutputStream(multiChunkFile));
							
							// DB
							multiChunkEntry = db.createMultiChunk();
							multiChunkEntry.setChecksum(chunk.getChecksum());
						}
						
						// - Add chunk data
						multiChunk.write(chunk);
						multiChunkEntry.addChunk(chunkEntry);
					}
					
					content.addChunk(chunkEntry);					
				}
				
				if (chunk != null) {
					content.setChecksum(chunk.getChecksum());
				}
				else {
					content.setChecksum(null);
				}
				
				fileVersion.setContent(content);
				db.addContent(content);
			}
			
			fileHistory.addVersion(fileVersion);			
			db.addFileHistory(fileHistory);
		}
		
		// Save database
		db.save(localDatabaseFile, true, false);
		
		System.out.println(db);
	}

	
	public static Database load3() throws IOException {
		Database db = new Database();
		
		// Load database
        File dbFile = new File("/tmp/dbfile3");
		db.load(dbFile, false);

        System.out.println(db);
        
        return db;
	}
}
