## Java problem responses

I've chosen problems 2 and 4. For build and execution instructions, please visit the two project directories.

## Note on sequence data

I didn't opt to do problem 1, which concerns transforming DNA short reads into FASTA format. But I thought I'd mention that I've occasionally helped with tasks like this, and for instance I contributed in some very minor way to a paper:

[Transposon insertional mutagenesis in *Saccharomyces uvarum* reveals trans-acting effects influencing species-dependent essential genes](https://genome.cshlp.org/content/early/2019/01/11/gr.232330.117)

This was just a quick script to transform some data, and I was surprised to find my name in the acknowledgments. Here's a bit of documentation from the script, which took as input a set of mappings of short reads onto a target genome:

    # This script parses a list of read mappings in SAM format and produces genome
    # annotations, where each annotation represents an insertion point implied by
    # one or more read mappings. The annotations are generated in GFF3 and bedGraph
    # formats. The script also displays insertion point counts for each chromosome
    # in the genome.


