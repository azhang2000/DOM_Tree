package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {	
		String temp = sc.nextLine();
		root = new TagNode(temp.substring(1,temp.length()-1),null,null);
		temp = sc.nextLine();
		root.firstChild = new TagNode(temp.substring(1,temp.length()-1),null,null);
		root.firstChild.firstChild = build(root.firstChild.firstChild);
	}
	
	private TagNode build(TagNode root) {
		String node;
		if(sc.hasNext())
		{
			node = sc.nextLine();
		}
		else
			return null;
		
		if(isTag(node) && !node.contains("/"))
		{
			root = new TagNode(node.substring(1,node.length()-1), null, null);
			root.firstChild = build(root.firstChild);
		}
		else if(isTag(node) && node.contains("/"))
			return null;
		else
			root = new TagNode(node, null, null);
		
		root.sibling = build(root.sibling);
		
		return root;
	}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		replaceTag(oldTag, newTag, root);
	}
	
	private void replaceTag(String oldTag, String newTag, TagNode root)
	{
		if(root!=null)
		{
		if(root.firstChild !=null)
		{
			if(root.tag.equals(oldTag))
				root.tag=newTag;
			replaceTag(oldTag,newTag,root.firstChild);
		}
		
		replaceTag(oldTag,newTag,root.sibling);
		}
		
	}
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		boldRow(root, row, 0);
	}
	
	private void boldRow(TagNode root, int row, int level)
	{
		
		if (root!=null)
		{
			if(root.tag.equals("tr") && root.firstChild!=null)
				level++;
			boldRow(root.firstChild, row, level);
			boldRow(root.sibling,row,level);
			if(level==row && root.tag.equals("td") && root.firstChild!=null)
			{
				root.firstChild = new TagNode("b",root.firstChild,null);
			}
		}
	}
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		removeTag(root.firstChild,root,tag);
	}
	
	private void removeTag(TagNode root, TagNode prev, String tag)
	{
		if(root!=null)
		{
		removeTag(root.firstChild,root,tag);
		removeTag(root.sibling,root,tag);
		if(root.tag.equals(tag)&&root.firstChild!=null)
		{

			if(tag.equals("ol")|| tag.equals("ul"))
			{
			TagNode ptr2 = root.firstChild;
			while(ptr2!=null)
			{
				if(ptr2.tag.equals("li")&&ptr2.firstChild!=null)
					ptr2.tag="p";
				ptr2=ptr2.sibling;
			}
			}
			
			if(root==prev.sibling)
			{
				prev.sibling=root.firstChild;
				TagNode ptr = root.firstChild.sibling;
				while(ptr!=null && ptr.sibling!=null)
					ptr=ptr.sibling;
				if(ptr!=null)
				ptr.sibling=root.sibling;
				if(ptr==null)
					root.firstChild.sibling=root.sibling;
			}
			else if(root==prev.firstChild)
			{
				prev.firstChild=root.firstChild;
				TagNode ptr = root.firstChild.sibling;
				while(ptr!=null && ptr.sibling!=null)
					ptr=ptr.sibling;
				if(ptr!=null)
				ptr.sibling=root.sibling;
				if(ptr==null)
					root.firstChild.sibling=root.sibling;
			}
		
			
		}
		}
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		addTag(root, word, tag, "");
	}
	private void addTag(TagNode root, String word, String tag, String prevTag)
	{
		if(root!=null)
		{
			addTag(root.firstChild,word,tag,root.tag);
			addTag(root.sibling,word,tag,prevTag);
			if(root.firstChild==null && !prevTag.equals(tag)&& isMatch(root.tag,word)) {
				int index = root.tag.toLowerCase().indexOf(word.toLowerCase());
				String word2 = root.tag.substring(index,index+word.length());
				if(index+word2.length()<root.tag.length() && root.tag.charAt(index+word2.length())!=' ')
					word2=word2 + root.tag.charAt(index+word2.length());
				String end = root.tag.substring(index+word2.length());
				root.tag = root.tag.substring(0,index);
				TagNode ptr = root.sibling;
				if(index==0)
				{
				root.tag=tag;
				root.firstChild=new TagNode(word2,null,null);
				}
				else
					root.sibling = new TagNode(tag,new TagNode(word2,null,null),ptr);
				if(end.length()>0)
				{
					if(index!=0)
					{
					root.sibling.sibling = new TagNode(end,null,ptr);
					TagNode temp = root.sibling.sibling;
					addTag(temp,word,tag,"");
					}
					else
					{root.sibling=new TagNode(end,null,ptr);
					TagNode temp = root.sibling;
					addTag(temp,word,tag,"");
					}
				}
			}
		}
	}
	
	private boolean isMatch(String tag, String target)
	{
		String [] tags = tag.split(" ");
		for(int i=0; i<tags.length;i++)
		{
			if((tags[i].toLowerCase()).equals(target.toLowerCase()))
				return true;
			if((tags[i].toLowerCase()).matches(target.toLowerCase()+"(\\.|\\?|:|!|;|,)") && tags[i].length()==target.length()+1)
				return true;
		}
		
		return false;
	}
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	private boolean isTag(String s)
	{
		if(s.matches("<.+>"))
			return true;
		return false;
	}
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}