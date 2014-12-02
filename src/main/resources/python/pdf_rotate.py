# usage
# > python pdf_rotate.py input_file.pdf full/output/dir
# > python (script file) (input file)   (output path)
# >         0             1              2	

from PyPDF2 import PdfFileWriter, PdfFileReader
import sys
import re
import os

if(len(sys.argv) <= 1):
    print 'arguments too short, exiting!'
    exit()

print 'Processing PDF file: ' + sys.argv[1]

pdfIn = PdfFileReader(file(sys.argv[1], 'rb'))
pdfOut = PdfFileWriter()


for pageNum in range(0, pdfIn.getNumPages()):
    pdfOut.addPage(pdfIn.getPage(pageNum).rotateClockwise(180))

try: 
    os.makedirs(sys.argv[2])
except OSError:
    if not os.path.isdir(sys.argv[2]):
        raise

outputStream = file(sys.argv[2] + os.path.split(sys.argv[1])[1], "wb")
pdfOut.write(outputStream)
outputStream.close()
