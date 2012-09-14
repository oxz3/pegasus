#ifndef MASTER_H
#define MASTER_H

#include <list>
#include <vector>

#include "engine.h"
#include "dag.h"

using std::string;
using std::vector;
using std::priority_queue;
using std::list;

class Host {
public:
    string host_name;
    unsigned int memory;
    unsigned int cpus;
    unsigned int slots;
    
    Host(const string &host_name, unsigned int memory, unsigned int cpus) {
        this->host_name = host_name;
        this->memory = memory;
        this->cpus = cpus;
        this->slots = 1;
    }
    
    void log_status();
};

class Slot {
public:
    unsigned int rank;
    Host *host;
    
    Slot(unsigned int rank, Host *host) {
        this->rank = rank;
        this->host = host;
    }
};

class TaskPriority {
public:
    bool operator ()(const Task *x, const Task *y){
        return x->priority < y->priority;
    }
};

typedef priority_queue<Task *, vector<Task *>, TaskPriority> TaskQueue;

typedef list<Slot *> SlotList;
typedef list<Task *> TaskList;

class Master {
    string program;
    string dagfile;
    string outfile;
    string errfile;
    DAG *dag;
    Engine *engine;
    
    FILE *task_stdout;
    FILE *task_stderr;
   
    FILE *resource_log;

    vector<Slot *> slots;
    vector<Host *> hosts;
    SlotList free_slots;
    TaskQueue ready_queue;
    
    int numworkers;
    double max_wall_time;
    
    long total_count;
    long success_count;
    long failed_count;
    
    unsigned total_cpus;
    double total_runtime;
    
    bool has_host_script;
    
    double start_time;
    double finish_time;
    double wall_time;

    unsigned cpus_avail;
    unsigned memory_avail;
    unsigned slots_avail;
    
    void register_workers();
    void schedule_tasks();
    void wait_for_results();
    void process_result();
    void queue_ready_tasks();
    void submit_task(Task *t, int worker);
    void merge_all_task_stdio();
    void merge_task_stdio(FILE *dest, const string &src, const string &stream);
    void write_cluster_summary(bool failed);

    void allocate_resources(Host *host, unsigned cpus, unsigned memory);
    void release_resources(Host *host, unsigned cpus, unsigned memory);
    void log_resources(unsigned slots, unsigned cpus, unsigned memory, const string &hostname);
public:
    Master(const string &program, Engine &engine, DAG &dag, const string &dagfile, 
        const string &outfile, const string &errfile, bool has_host_script = false, 
        double max_wall_time = 0.0, const string &resourcefile = "");
    ~Master();
    int run();
};

#endif /* MASTER_H */
