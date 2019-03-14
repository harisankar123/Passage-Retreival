in_qid  = False
in_qstr = False
qid = ""
qstr = ""
fnew = open("queries","w")
with open("QUERIES_for_training.txt") as file:
	for line in file:
		line = line.strip()
		if len(line)>0:
			if line.startswith("<DOCNO>"):
				in_qid = True
				in_qstr = False
				fnew.write(qid+" "+qstr+"\n")
				qstr = ""
			elif line.startswith("</DOCNO>"):
				in_qid = False
				in_qstr = True
			elif in_qid:
				qid = line
			elif in_qstr:
				line = line.replace("/"," ")
				if len(qstr) > 0:
					qstr += " " + line
				else:
					qstr = line

fnew.write(qid+" "+qstr+"\n")
fnew.close()
