
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class busSystem {

	public static void main(String[] args) {
		System.out.print("Welcome to my bus management systemsystem.");
		Scanner input = new Scanner( System.in );
		boolean quit = false;
		while(!quit) {
			System.out.print("What do you want? (Enter 1 for shortst path, 2 for serching bus stop"
					+ "3 for fiding trips"
					+ "or enter quit) \n:");
			if(input.hasNext("quit")){
				quit=true;
			}
			if (input.hasNextInt()) {
				int k=input.nextInt();
				boolean end =false;
				switch(k) {
				case 1:
					while(!end) {
						System.out.print("Enter your start stop ID or quit");
						String src=input.next();
						if(src.equals("quit")) break;
						System.out.print("Enter your goal stop ID or quit");
						String des = input.next();
						if(des.equals("quit")) break;
						shortest(src,des,end);
					}
					quit=true;
					break;
				case 2:
					while(!end) {
						System.out.print("Enter the first characters of the bus stop you are looking for or enter quit");
						input.useDelimiter("\n"); 
						String pref=input.next();
						if(pref.equals("quit")) break;
						List<stop> busStopList=findBusStop(pref,end);
						if(busStopList.isEmpty()) {
							System.out.print("Not found\n");
						}
						else {
							for(int i=0;i<busStopList.size();i++)
							{
								System.out.print(busStopList.get(i)+"\n");
							}
							end=true;
							break;
						}
					  
					}
					quit=true;
					break;
				case 3:
					while(!end)
						{
						System.out.print("Enter your arrival time for example 12:30:30");
						String scr=input.next();
						String[] time=scr.split(":");
						if(time.length!=3) {
							System.out.print("Error, please enter like the example\n");
						}
						else {
							if((Integer.parseInt(time[0])<24&&Integer.parseInt(time[1])<60&&Integer.parseInt(time[2])<60)){
							System.out.print("trips arriving at the time are as follows\n");
							List<String[]> trips=stopTimes(scr);
							for(int i=0;i<trips.size();i++) {
								System.out.print(Arrays.toString(trips.get(i)));
							}
							quit=true;
							end=true;
							break;
						}
							else {
								System.out.print("Erorr please notie that arrival time must be between 00:00:00 and 23:59:59");
							}
						}
					}
					break;

				default:
					System.out.print("Error - Enter 1,2,3 or quit.\n");
					input.next();
				}
			}
		}
		
	}
	public static void shortest(String src,String des,boolean suc) {
		String p =null;
		ArrayList<String> IDs=new ArrayList<>();
		List<stop> stops = findBusStop(p,suc);
		for(int i=0;i<stops.size();i++) {
			IDs.add(stops.get(i).ID);
		}
		Map <String,travel> map=makeMap();
		if(!map.containsKey(src)) {
			System.out.print("Error--Enter the collect ID\n");
			suc=false;
		}
		else {
		String[] towns =map.keySet().toArray(new String[map.keySet().size()]);
		int[] via = new int[towns.length];
		Map <String,Integer> distance =new HashMap<>();
		List <Integer> cost = new ArrayList<>();
		dijkstra(map,src,towns,distance,via,cost);
		if(!distance.containsKey(des)) {
			System.out.print("Error--Enter the collect ID\n");
			suc=false;
		}else {
		int ans = distance.get(des);
		if(ans==Integer.MAX_VALUE) {
			System.out.print("thre is no way from"+src+"to"+des+" \n");
			suc=true;
		}
		else {
		System.out.print("the total cost is" + ans+"\n");
		System.out.print("via");
		 for (int i=Integer.parseInt(des); i!=Integer.parseInt(src); i=via[i]) {
			 if(i!=0)	{	
			 System.out.print(i + " ");
			 System.out.println(src);
		  }
		  }
		 suc=true;
		 }
		}
		}
	}
	static public Map <String,travel> makeMap() {
		Map <String,travel> mapData = new HashMap<>();
	    ArrayList<String> transfer = readFile("transfers.txt");
	    for (int i=0; i<transfer.size(); i++) {
	    	String [] sp =transfer.get(i).split(",");
	    	String name =sp[0];
	    	if(!mapData.containsKey(name)) {
	    		travel tr=new travel(name);
	    		mapData.put(name,tr);
	    	}
	    	if(sp.length==3){//type 0
	    		mapData.get(name).add(sp[1],2);
	    	}
	    	else {
	    		mapData.get(name).add(sp[1],Integer.parseInt(sp[3])/100);
	    	}
	    }
	      
	    ArrayList<String> stopTime = readFile("stop_times.txt");
	    for (int i=1; i<stopTime.size()-1; i++) {
	    	String [] line1 =stopTime.get(i).split(",");
	    	String [] line2 =stopTime.get(i+1).split(",");
	    	String name =line1[3];
	    	if(!mapData.containsKey(name)) {
	    		travel tr=new travel(name);
	    		mapData.put(name,tr);
	    	}
	    	if(line1[0]==line2[0]){//same ID
	    		mapData.get(name).add(line2[3],1);;
	    	}
	    }
	    return mapData;
	}
	 
	static public ArrayList<String> readFile(String fileName) {
		 ArrayList<String> list = new ArrayList<String>();
		 try(BufferedReader br = new BufferedReader(new FileReader(fileName))) 
         {
             String line;
             int k=0;
             while ((line = br.readLine()) != null) {
            	 if(k>=1) {
            		 list.add(line);
            	 }
            	 k++;
            	 }
             }
         catch (IOException e) {
             System.out.println("An error occurred.");
             e.printStackTrace();
         }
		 return list;
	}
	 public static void dijkstra(Map <String,travel> map,String src,String[]towns,Map <String,Integer> distance,int[] via,List<Integer>cost) {
	    	int nTown = towns.length;
	    	Map <String,String> fixed = new HashMap<>();
	    	for (int i=0; i<nTown; i++) { 
	    	    distance.put(towns[i],Integer.MAX_VALUE); 
	    	    fixed.put(towns[i],"false");	
	    	}
	    	distance.replace(src,0);
	    	while (true) {
	    	    String marked = minIndex(distance,fixed,towns);
	    	    if (marked == null) return; 
	    	    if (distance.get(marked)==Integer.MAX_VALUE) {
	    	    	return;
	    	    }
	    	    fixed.replace(marked,"true");
	    	    for (int j=0; j<nTown; j++) {
	    	    	if(map.get(marked).map.containsKey(towns[j])) {
	    	    	if (map.get(marked).get(towns[j])>0 && fixed.get(towns[j])=="false") {
	    		    int newDistance = distance.get(marked)+map.get(marked).get(towns[j]);
	    		    if (newDistance < distance.get(towns[j])) {
	    		    	distance.replace(towns[j],newDistance);
	    		    	via[j]=Integer.parseInt(marked);
	    		    }
	    		}
	    	   }
	    	}
	    }
	   }
	        	public static String minIndex(Map <String,Integer> distance,Map <String,String> fixed,String[] towns) {
	    	int i=0;
		    for (;i<towns.length;i++) {
	    		String cur =towns[i];
	    	if (fixed.get(cur)=="false") break;
	    	}
	    	if (i == towns.length) return null; 
	    	for (int k=i+1; k<towns.length; k++) 
	    	    if (fixed.get(towns[k])=="false" && distance.get(towns[k])<distance.get(towns[i])) i=k;
	    	return towns[i];
	}
	        	
	        	
	public static List<String[]> stopTimes(String input) {
		ArrayList<String> stopTime = readFile("stop_times.txt");
		Map<Integer,String[]> arrivalTimeMap=new HashMap<>();
		 for (int i=0; i<stopTime.size(); i++) {
		    	String [] sp =stopTime.get(i).split(",");
		    	String fo =sp[1].replace(" ", "");
		    	if(input.equals(fo)) {
		    		arrivalTimeMap.put(Integer.parseInt(sp[0]),sp);
		    	}
		 }
		 List<Integer> IDs =new ArrayList<>(arrivalTimeMap.keySet());
		 Collections.sort(IDs);
		 ArrayList<String[]> sorted =new ArrayList<>();
		 for(int i= 0;i<IDs.size();i++) {
			 sorted.add(arrivalTimeMap.get(IDs.get(i)));
		 }
		return sorted;
	}  
	public static List<stop> findBusStop(String pref,boolean end){
		Tst tst = new Tst();
		String file = "stops.txt";
		Map <String,stop> stopMap = new HashMap<>();
		 try(BufferedReader br = new BufferedReader(new FileReader(file))) 
         {
             String line;
             while ((line = br.readLine()) != null) {
             String [] sp =line.split(",");
             stop st=new stop(sp);
             String name=sp[2];
             if(name.startsWith("FLAGSTOP")) {
            	 String change =name.replace("FLAGSTOP","");
            	 name=change+"FLAGSTOP";
             }
             else if(name.startsWith("WB")) {
            	 String change =name.replace("WB","");
            	 name=change+"WB";
             }
             else if(name.startsWith("NB")) {
            	 String change=name.replace("NB","");
            	 name=change+"NB";
             }
             else if(name.startsWith("SB")) {
            	 String change=name.replace("SB","");
            	 name=change+"SB";
             }
             else if(name.startsWith("EB")) {
            	 String change=name.replace("EB","");
            	 name=change+"EB";
             }
             stopMap.put(name,st);
             tst.add(name);
             }
         }
         catch (IOException e) {
             System.out.println("An error occurred.");
             e.printStackTrace();
         }
		 List<stop> towns=new ArrayList<>();
		 if(pref!=null) {
			 Iterable <String> co =tst.root.keysWithPrefix(pref);
			 for(String s:co) {
				 towns.add(stopMap.get(s));
			 }
		 }
		 else {
			towns=new ArrayList<stop>(stopMap.values());
			}
		 end=true;
		 return towns;
	}

}
class stop{
	String ID;
	String stopCode;
	String stopName;
	String stopDesc;
	String stopLat;
	String stopLon;
	String zoneCode;
	String stopURL;
	String stopLocation;
	String parentStop;
	stop(String [] string){
		ID =string[0];
		stopCode =string[1];
		stopName =string[2];
		stopDesc=string[3];
		stopLat=string[4];
		stopLon =string[5];
		zoneCode=string[6];
		stopURL=string[7];
		stopLocation=string[8];
		
	}
	@Override
	public String toString() {
		String re=(ID+" "+stopCode+" "+stopName+" "+stopDesc+" "+stopLat+" "+stopLon+" "+zoneCode+" "+stopURL+" "+stopLocation);
		return re;
	}
	
}
class Tst {

    public TstNode root;
    public Tst() {
        root = null;
    }

    public boolean add(String element) {
        if (element == null || element.isEmpty()) {
            throw new IllegalArgumentException("Keys must be non-null and non-empty");
        }
       
        try {
            if (root == null) {
                root = new TstNode(element);
                return true;
            } else {
                return root.add(element) != null;
            }
        } finally {
           
        }
    }
    
}
class TstNode {

    private Character character;
    boolean storesKey;

    private TstNode left;
    private TstNode middle;
    private TstNode right;
    

    public TstNode(String key) {
        this(key, 0);
    }

    public TstNode(String key, int charIndex) {
        if (charIndex >= key.length()) {
            throw new IndexOutOfBoundsException();
        }
        character = key.charAt(charIndex);
        left = right = null;
        if (charIndex + 1 < key.length()) {
            // Stores the rest of the key in a midlle-link chain
            storesKey = false;
            middle = new TstNode(key, charIndex + 1);
        } else {
            middle = null;
            storesKey = true;
        }
    }
        public TstNode add(String key) {
            return this.add(key,0);
        }

        private TstNode add(String key,int charIndex) {
            if (charIndex < key.length()) {
                Character c = key.charAt(charIndex);
                if (character.equals(c)) {
                    if (charIndex == key.length() - 1) {
                        if (storesKey) {
                            return null;
                        } else {
                            storesKey = true;
                            return this;
                        }
                    } else if (this.middle != null) {
                        return middle.add(key, charIndex + 1);
                    } else {
                        this.middle = new TstNode(key, charIndex + 1);
                        return middle;
                    }
                } else if (c.compareTo(character) < 0) {
                    if (this.left != null) {
                        return left.add(key, charIndex);
                    } else {
                        left = new TstNode(key, charIndex);
                        return left;
                    }
                } else {
                    if (this.right != null) {
                        return right.add(key, charIndex);
                    } else {
                        right = new TstNode(key, charIndex);
                        return right;
                    }
                }
            } else {
                throw new IllegalArgumentException("CharIndex out of bound " + charIndex + ", " + key);
            }
    }
        
        private TstNode getNodeFor(String key, int charIndex) {
            if (charIndex >= key.length()) {
                return null;
            }
            Character c = key.charAt(charIndex);
            if (c.equals(this.character)) {
                if (charIndex == key.length() - 1) {
                    return this;
                } else {
                    return this.middle == null ? null : this.middle.getNodeFor(key, charIndex + 1);
                }
            } else if (c.compareTo(this.character) < 0) {
                return left == null ? null : left.getNodeFor(key, charIndex);
            } else {
                return right == null ? null : right.getNodeFor(key, charIndex);
            }
        }

        public List<String> keys() {
            List<String> keys = Collections.synchronizedList(new ArrayList<>());
            this.keys("", keys);
            return keys;
        }

        private void keys(String currentPath, List<String> keys) {
            if (this.storesKey) {
                keys.add(currentPath + this.character);
            }
            // For left and right branches, we must not add this node's character to the path
            if (left != null) {
                left.keys(currentPath, keys);
            }
            if (right != null) {
                right.keys(currentPath, keys);
            }
            // For the middle child, instead, this node's character is part of the path forward
            if (middle != null) {
                middle.keys(currentPath + character, keys);
            }
        }

        public Iterable<String> keysWithPrefix(String prefix) {
            // Invariant: prefix is not empty
            TstNode node = this.getNodeFor(prefix, 0);
            return node == null
                    ? new HashSet<>()
                    : node.keys().stream()
                    // All keys in node.keys already include the last character in prefix
                    .map(s -> prefix.substring(0, prefix.length() - 1) + s)
                    .collect(Collectors.toSet());
        }
   
}
class travel{
	String name;
	Map<String,Integer> map;
	public travel(String name) {
		this.name=name;
		this.map=new HashMap<>();
	}
	public void add(String stop,int cost) {
		map.put(stop,cost);
	}
	public int get(String des) {
		return map.get(des);
	}
	
}
	
