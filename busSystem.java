# busStop

import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class busSystem {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Tst tst = new Tst();
		String file = "stops.txt";
		Map <Integer,stop> stopMap = new HashMap<>();
		Integer i=0;
		 try(BufferedReader br = new BufferedReader(new FileReader(file))) 
         {
             String line;
             while ((line = br.readLine()) != null) {
             String [] sp =line.split(",");
             stop st=new stop(sp);
             stopMap.put(i, st);
             i++;
             }
         }
         catch (IOException e) {
             System.out.println("An error occurred.");
             e.printStackTrace();
         }
		 stop s =stopMap.get(2);
		 System.out.print(s.stopName);
		 

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
		parentStop=null;
		
	}
	
}
class Tst {

    private TstNode root;
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
