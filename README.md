Assignment 4 Report: Smart City Scheduling Analysis
1. Data Summary
Dataset	Nodes	Edges	SCC Count	Graph Type	Weight Model
small1	3	3	1	Cyclic (single SCC)	Edge weights
small2	4	4	4	DAG	Edge weights
small3	5	6	3	Mixed	Edge weights
medium1	7	8	3	Mixed	Edge weights
medium2	10	11	10	DAG	Edge weights
medium3	8	10	5	Mixed	Edge weights
large1	20	21	16	Mixed	Edge weights
large2	20	17	20	DAG	Edge weights
large3	20	23	16	Mixed	Edge weights
Weight Model: Edge weights were used for all shortest path calculations, with weights ranging from 1-5 as shown in the condensation graphs.

2. Algorithm Implementation Results
2.1 Strongly Connected Components (Tarjan's Algorithm)
Dataset	Nodes	Time (ns)	SCC Count	Max SCC Size
small1	3	16,800	1	3
small2	4	13,700	4	1
small3	5	18,000	3	2
medium1	7	26,100	3	3
medium2	10	34,500	10	1
medium3	8	20,300	5	2
large1	20	7,019,100	16	3
large2	20	44,100	20	1
large3	20	62,000	16	3
Observations:

Tarjan's algorithm shows linear time complexity O(V+E) in most cases

Large1 shows unusually high execution time (7ms) despite similar node count to other large datasets

Pure DAGs (small2, medium2, large2) have maximum SCC size of 1 (each node is its own component)

2.2 Topological Sorting (Kahn's Algorithm)
Dataset	Condensation Nodes	Pushes	Pops	Time (ns)
small1	1	1	1	9,000
small2	4	4	4	24,000
small3	3	3	3	9,200
medium1	3	3	3	11,700
medium2	10	10	10	41,500
medium3	5	5	5	9,200
large1	16	16	16	6,004,700
large2	20	20	20	67,300
large3	16	16	16	65,200
Observations:

Kahn's algorithm efficiently handles DAGs with time complexity O(V+E)

Pushes and pops equal the number of nodes in condensation graph

Consistent performance across different graph sizes when normalized by node count

2.3 Shortest Paths in DAG
Dataset	Source Comp	Relaxations	Time (ns)	Critical Path Length
small1	0	0	12,000	0
small2	3	3	13,800	5
small3	2	2	69,600	4
medium1	2	2	9,400	6
medium2	9	10	19,800	12
medium3	4	4	16,600	9
large1	15	15	11,464,600	37
large2	6	6	37,100	21
large3	15	15	29,900	36
Observations:

Number of relaxations correlates with longest path from source in condensation graph

Critical path length increases with graph complexity and connectivity

Large variations in execution time not always correlated with graph size

3. Analysis
3.1 SCC Performance Analysis
Bottlenecks:

DFS recursion depth in Tarjan's algorithm

Stack operations for maintaining lowlink values

Memory access patterns for graph traversal

Effect of Graph Structure:

Density: Denser graphs show more complex SCC structures

SCC Sizes: Larger SCCs (like in small1, medium1) indicate stronger connectivity

Pure DAGs: Each node forms its own SCC, simplifying condensation

3.2 Topological Sorting Insights
Kahn's Algorithm Efficiency:

Excellent performance for sparse graphs

Linear scaling with number of nodes in condensation graph

Minimal memory overhead compared to DFS-based approaches

3.3 DAG Shortest Path Characteristics
Performance Patterns:

Single-source shortest paths efficiently computed using topological order

Critical path analysis reveals task dependencies and scheduling constraints

Edge weight distribution affects path length variability

4. Conclusions and Recommendations
4.1 Method Selection Guidelines
Scenario	Recommended Algorithm	Rationale
Cyclic dependency detection	Tarjan's SCC	Handles all graph types, provides component structure
Pure DAG scheduling	Kahn's Topological Sort	Simple, efficient, natural task ordering
Task duration optimization	DAG Shortest Path	Finds critical paths and optimal schedules
Mixed cyclic/acyclic	SCC + Condensation	Reduces complexity before other operations
4.2 Practical Recommendations
For Smart City Scheduling:

Use SCC compression first to identify and handle cyclic dependencies

Apply topological sorting to the condensation DAG for task sequencing

Utilize critical path analysis for resource allocation and deadline management

Performance Considerations:

Tarjan's algorithm preferred over Kosaraju for memory efficiency

Kahn's algorithm optimal for DAGs with known in-degree counts

Dynamic programming on topological order provides optimal path solutions

Implementation Strategy:

Modular design separating SCC, topological sort, and path algorithms

Instrumentation for performance monitoring across different graph sizes

Comprehensive testing with varied graph structures

4.3 Key Findings
SCC compression effectively reduces complex dependency graphs to manageable DAGs

Topological ordering enables predictable task scheduling in acyclic components

Critical path analysis identifies scheduling bottlenecks and optimization opportunities

The hybrid approach (SCC + DAG algorithms) successfully handles real-world mixed dependency scenarios

This implementation demonstrates robust handling of smart city scheduling problems, providing both theoretical guarantees and practical efficiency for large-scale task dependency resolution.