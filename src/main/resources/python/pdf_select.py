# usage
# > python pdf_select.py input_file.pdf full/output/dir  23-56 65 23
# > python (script file) (input file)   (output path)    (space delimited ranges of pages to keep)
# >        0			 1				2			 	 3..n

from PyPDF2 import PdfFileWriter, PdfFileReader
import sys
import re
import os

if(len(sys.argv) <= 1):
	print 'arguments too short, exiting!'
	exit()

print 'Processing PDF file:  ' + sys.argv[1]

pdfIn = PdfFileReader(file(sys.argv[1], "rb"))
pdfOut = PdfFileWriter()

pagesToKeep = []
p = re.compile('(\d+)-(\d+)')
ps = re.compile('(\d+)')

for page in sys.argv[3:]:
    if(p.search(page)):
        for x in range(int(p.search(page).group(1)) - 1, int(p.search(page).group(2))):
            pagesToKeep.append(x)
    elif(ps.search(page)):
        pagesToKeep.append(int(ps.search(page).group(1)) - 1)

for i in pagesToKeep:
    pdfOut.addPage(pdfIn.getPage(i))

try: 
    os.makedirs(sys.argv[2])
except OSError:
    if not os.path.isdir(sys.argv[2]):
        raise

outputStream = file(sys.argv[2] + os.path.split(sys.argv[1])[1], "wb")
pdfOut.write(outputStream)
outputStream.close()