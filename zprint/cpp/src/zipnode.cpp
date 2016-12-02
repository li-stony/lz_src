#include "zipnode.h"

ZipNode::ZipNode()
{

}

void ZipNode::add_size(long s, long cs)
{
	this->size += s;
	this->csize += cs;
	if (parent != NULL) {
		parent->add_size(s, cs);
	}
}
bool ZipNode::add_child(std::shared_ptr<ZipNode> child)
{
	std::string cname = child->get_name();
	std::size_t index = cname.find(this->name);
	if (index == 0) {
		if ((cname[this->name.length()] == '/') 
			|| (this->name == "/")) {
			std::size_t next_slash = cname.find('/', this->name.length() + 1);
			if (next_slash == std::string::npos) {
				// it's a direct child
				child->set_parent(this);
				this->children.push_back(child);
				return true;
			}
			else {
				// it'a a grandchild
				std::string pname = cname.substr(0, next_slash);
				std::shared_ptr<ZipNode> c_parent(nullptr);
				for (int i = 0; i < this->children.size(); i++) {
					if (this->children[i]->get_name() == pname) {
						c_parent = this->children[i];
					}
				}
				if (c_parent == nullptr) {
					ZipNode* p = new ZipNode();
					p->name = pname;
					c_parent = std::shared_ptr<ZipNode>(p);
					c_parent->set_parent(this);
					this->children.push_back(c_parent);
				}
				c_parent->add_child(child);
			}
		} 
		else {
			// it's not child, but they both has a same prefix.
			// like '/a/bc' and '/a/bcd/a.txt' .
			return false;
		}
	}
	else {
		return false;
	}
	
}

std::string ZipNode::get_name()
{
	return this->name;
}
uint64_t ZipNode::get_size()
{
	return this->size;
}
uint64_t ZipNode::get_csize()
{
	return this->csize;
}

void ZipNode::set_name(std::string n) 
{
	if (n[0] != '/') {
		n.insert(0, "/");
	}
	if (n[n.length() - 1] == '/') {
		n.erase(n.length() - 1);
	}
	this->name = n;
}

void ZipNode::set_parent(ZipNode* p)
{
	parent = p;
}
std::vector<std::shared_ptr<ZipNode>> ZipNode::get_children()
{
	return children;
}
int ZipNode::get_leaf_num() 
{
	if (children.size() == 0) {
		return 1;
	}
	int num = 0;
	for (int i = 0; i < children.size(); i++) {
		num = num + children[i]->get_leaf_num();
	}
	return num;
}